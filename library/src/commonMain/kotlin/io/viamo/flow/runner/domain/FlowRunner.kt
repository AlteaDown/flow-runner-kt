package io.viamo.flow.runner.domain

import Prompt
import ValidationException
import io.viamo.flow.runner.block.IBlock
import io.viamo.flow.runner.block.IBlockExit
import io.viamo.flow.runner.block.type.log.ILogBlock
import io.viamo.flow.runner.block.type.log.LogBlockRunner
import io.viamo.flow.runner.block.type.message.IMessageBlock
import io.viamo.flow.runner.block.type.message.MessageBlockRunner
import io.viamo.flow.runner.block.type.numeric.INumericResponseBlock
import io.viamo.flow.runner.block.type.numeric.NumericResponseBlockRunner
import io.viamo.flow.runner.block.type.open.IOpenResponseBlock
import io.viamo.flow.runner.block.type.open.OpenResponseBlockRunner
import io.viamo.flow.runner.block.type.output.IOutputBlock
import io.viamo.flow.runner.block.type.output.OutputBlockRunner
import io.viamo.flow.runner.block.type.print.IPrintBlock
import io.viamo.flow.runner.block.type.print.PrintBlockRunner
import io.viamo.flow.runner.block.type.run_flow.IRunFlowBlock
import io.viamo.flow.runner.block.type.run_flow.RunFlowBlock
import io.viamo.flow.runner.block.type.run_flow.RunFlowBlockRunner
import io.viamo.flow.runner.block.type.select_many.ISelectManyResponseBlock
import io.viamo.flow.runner.block.type.select_many.SelectManyResponseBlockRunner
import io.viamo.flow.runner.block.type.select_one.ISelectOneResponseBlock
import io.viamo.flow.runner.block.type.select_one.SelectOneResponseBlockRunner
import io.viamo.flow.runner.block.type.selectcase.CaseBlockRunner
import io.viamo.flow.runner.block.type.selectcase.ICaseBlock
import io.viamo.flow.runner.block.type.set_group_membership.ISetGroupMembershipBlock
import io.viamo.flow.runner.block.type.set_group_membership.SET_GROUP_MEMBERSHIP_BLOCK_TYPE
import io.viamo.flow.runner.block.type.set_group_membership.SetGroupMembershipBlockRunner
import io.viamo.flow.runner.collections.pop
import io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.BasicBacktrackingBehaviour
import io.viamo.flow.runner.domain.behaviours.BehaviourConstructor
import io.viamo.flow.runner.domain.behaviours.IBehaviour
import io.viamo.flow.runner.domain.behaviours.IBehaviourConstructor
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.domain.runners.*
import io.viamo.flow.runner.flowspec.*
import kotlinx.datetime.*
import kotlinx.serialization.json.JsonObject

typealias BlockRunnerFactoryStore = Map<String, TBlockRunnerFactory>

interface IFlowNavigator {
  suspend fun navigateTo(block: IBlock): IRichCursor
}

interface IPromptBuilder {
  suspend fun buildPromptFor(block: IBlock, interaction: IBlockInteraction): BasePrompt<*>?
}

val DEFAULT_BEHAVIOUR_TYPES = listOf(
  BehaviourConstructor(
    name = "BasicBacktrackingBehaviour",
    new = { context, navigator, promptBuilder -> BasicBacktrackingBehaviour(context, navigator, promptBuilder) }
  )
)

/**
 * Block types that do not request additional input from an "io.viamo.flow.runner."flow-spec".IContact"
 */
val NON_INTERACTIVE_BLOCK_TYPES = listOf("Core.Case", "Core.RunFlow")

/**
 * A map of "io.viamo.flow.runner."flow-spec".IBlock.type" to an "TBlockRunnerFactory" function.
 */
fun createDefaultBlockRunnerStore(): Map<String, TBlockRunnerFactory> = mapOf(
  "MobilePrimitives.Message" to { block, context -> MessageBlockRunner(block as IMessageBlock, context) },
  "MobilePrimitives.OpenResponse" to { block, context -> OpenResponseBlockRunner(block as IOpenResponseBlock, context) },
  "MobilePrimitives.NumericResponse" to { block, context -> NumericResponseBlockRunner(block as INumericResponseBlock, context) },
  "MobilePrimitives.SelectOneResponse" to { block, context -> SelectOneResponseBlockRunner(block as ISelectOneResponseBlock, context) },
  "MobilePrimitives.SelectManyResponse" to { block, context -> SelectManyResponseBlockRunner(block as ISelectManyResponseBlock, context) },
  "Core.Case" to { block, context -> CaseBlockRunner(block as ICaseBlock, context) },
  "Core.Output" to { block, context -> OutputBlockRunner(block as IOutputBlock, context) },
  "Core.Log" to { block, context -> LogBlockRunner(block as ILogBlock, context) },
  "ConsoleIO.Print" to { block, context -> PrintBlockRunner(block as IPrintBlock, context) },
  "Core.RunFlow" to { block, context -> RunFlowBlockRunner(block as IRunFlowBlock, context) },
  SET_GROUP_MEMBERSHIP_BLOCK_TYPE to { block, context -> SetGroupMembershipBlockRunner(block as ISetGroupMembershipBlock, context) },
)

/**
 * Main interface into this library.
 * @see README.md for usage details.
 */
data class FlowRunner(
  /** Running context, JSON-serializable entity with enough information to start or resume a Flow. */
  override var context: Context,
  /** Map of block types to a factory producting an io.viamo.flow.runner.domain.runners.IBlockRunner instnace. */
  override val runnerFactoryStore: Map<String, TBlockRunnerFactory> = createDefaultBlockRunnerStore(),
  /** Instance used to "generate()" unique IDs across interaction history. */
  val idGenerator: IIdGenerator = IdGeneratorUuidV4(),
  /** Instances providing isolated functionality beyond the default runner, leveraging built-in hooks. */
  val behaviours: MutableMap<String, IBehaviour> = mutableMapOf(),
) : IFlowRunner, IFlowNavigator, IPromptBuilder {

  init {
    initializeBehaviours(DEFAULT_BEHAVIOUR_TYPES)
  }

  /**
   * Take list of constructors and initialize them like: """
   * runner.initializeBehaviours([MyFirstBehaviour, MySecondBehaviour])
   * runner.behaviours.myFirst instanceof MyFirstBehaviour
   * runner.behaviours.mySecond instanceof MySecondBehaviour
   * """ */
  private fun initializeBehaviours(behaviourConstructors: List<IBehaviourConstructor>) {
    behaviourConstructors.forEach { behaviourConstructor ->
      behaviours[behaviourConstructor.getNameAsKey()] = behaviourConstructor.new(context, this, this)
    }
  }

  private fun getNameAsKey(behaviourConstructor: IBehaviourConstructor) =
      behaviourConstructor.name.removeSuffix("Behaviour").removeSuffix("Behavior").replaceFirstChar { it.lowercase() }

  /**
   * Initialize entry point into this flow run; typically called internally.
   * Sets up first block, engages run state and entry timestamp on context.
   */
  override suspend fun initialize(): IRichCursor? {
    val block = findNextBlockOnActiveFlowFor() ?: throw ValidationException("Unable to initialize flow without blocks.")

    context.delivery_status = DeliveryStatus.IN_PROGRESS
    context.entry_at = createFormattedDate()

    // kick-start by navigating to first block
    return navigateTo(block)
  }

  /**
   * Verify whether or not we have a pointer in interaction history or not.
   * This identifies whether or not a run is in progress.
   * @param ctx
   */
  fun isInitialized() = context.cursor != null

  /**
   * Decipher whether or not cursor points to the first interactive block or not.
   */
  fun isFirst(): Boolean {
    return if (isInitialized()) {
      context.interactions.find { !NON_INTERACTIVE_BLOCK_TYPES.contains(it.type) }
        ?.let { it.uuid == context.cursor?.interactionId }
          ?: true
    } else {
      true
    }
  }

  /**
   * Decipher whether or not cursor points to the last block from interaction history.
   */
  fun isLast(): Boolean {
    return if (isInitialized()) {
      context.interactions.lastOrNull()?.uuid == context.cursor?.interactionId
    } else {
      true
    }
  }

  /**
   * Either begin or a resume a flow run, leveraging context instance member.
   */
  override suspend fun run(): IRichCursorInputRequired? {
    if (!isInitialized()) {
      initialize()
    }

    return runUntilInputRequiredFrom()
  }

  /**
   * Decipher whether or not calling run() will be able to proceed or our cursor's prompt is in an invalid state.
   * @param ctx
   */
  fun isInputRequiredFor(): Boolean {
    return when {
      context.cursor?.promptConfig == null -> false
      context.cursor?.promptConfig?.value == null -> true
      else -> try {
        val prompt = (hydrateRichCursorFrom() as IRichCursorInputRequired).prompt
        prompt.validate(prompt.value)
        false
      } catch (e: Exception) {
        e.printStackTrace()
        true
      }
    }
  }

  // todo: this could be findFirstExitOnActiveFlowBlockFor to an Expressions Behaviour
  //       ie. cacheInteractionByBlockName, applyReversibleDataOperation and reverseLastDataOperation
  fun cacheInteractionByBlockName(
    blockInteraction: IBlockInteraction,
    block: IMessageBlock
  ): Unit {
    val uuid = blockInteraction.uuid
    val entry_at = blockInteraction.entry_at
    val name = block.name
    val prompt = block.config.prompt

    if ("block_interactions_by_block_name" !in context.session_vars) {
      context.session_vars.put("block_interactions_by_block_name", JsonObject(emptyMap()))
    }

    // create a cache of "{[block.name]: {...}}" for subsequent lookups
    val blockNameKey = "block_interactions_by_block_name.${name}"
    val previous = context.session_vars[blockNameKey]
    // TODO: Implement backtracking
    /*
    val resource = prompt?.let { ResourceResolver(context).resolve(prompt) }
    val current = {
      __interactionId: uuid,
      time: entry_at,
      text: resource != null && resource.hasText() ? resource.getText() : '',
    }

    applyReversibleDataOperation({$set: {[blockNameKey]: current}}, {$set: {[blockNameKey]: previous}})*/
  }

  /**
   * Apply a mutation to "session_vars" and operations in both directions.
   * These vars are made available in content Expressions.
   * @param forward
   * @param reverse
   * @param context
   */
  override fun applyReversibleDataOperation(
    forward: Any,
    reverse: Any,
  ): Unit {
    //TODO: Implement backtracking
    /*context.session_vars = update(context.session_vars, forward)
    context.reversible_operations.push({
      interactionId: last(context.interactions)?.uuid,
      forward,
      reverse,
    })*/
  }

  /**
   * Pop last mutation to "session_vars" and apply its reversal operation.
   * @param context
   */
  // TODO: Implement backtracking
  //fun reverseLastDataOperation(context: IContext = this.context): IReversibleUpdateOperation? {
  //  if (context.reversible_operations.size == 0) {
  //    return null
  //  }
  //
  //  val lastOperation = context.reversible_operations.last() as IReversibleUpdateOperation
  //  context.session_vars = update(context.session_vars, lastOperation.reverse)
  //  return context.reversible_operations.pop()
  //}

  /**
   * Pushes onward through the flow when cursor's prompt has been fulfilled and there are blocks to draw from.
   * This will continue running blocks until an interactive block is encountered and input is required from
   * the io.viamo.flow.runner."flow-spec".IContact.
   * Typically called internally.
   * @param ctx
   */
  suspend fun runUntilInputRequiredFrom(): IRichCursorInputRequired? {
    var richCursor: IRichCursor = hydrateRichCursorFrom()

    do {
      if (isInputRequiredFor()) {
        println("Attempted to resume when prompt is not yet fulfilled; resurfacing same prompt instance.")
        return richCursor as IRichCursorInputRequired
      }
      runActiveBlockOn(richCursor, context.findBlockOnActiveFlowWith(richCursor.interaction.block_id))

      var block = findNextBlockOnActiveFlowFor()

      while (block == null && context.isNested()) {
        // nested flow complete, while more of parent flow remains
        block = stepOut()
      }

      if (block == null) {
        // bail-- we're done.
        continue
      }

      if (block.getType() == "Core.RunFlow") {
        check(block is RunFlowBlock)

        richCursor = navigateTo(block)
        block = stepInto(block)
      }

      if (block == null) {
        // bail-- we done.
        continue
      }

      richCursor = navigateTo(block)
    } while (block != null)

    complete(context)
    return null
  }

  // exitEarlyThrough(block: io.viamo.flow.runner."flow-spec".IBlock) {
  // todo: generate link from current interaction to exit block (flow.exitBlockId)
  // todo: raise if flow.exitBlockId not defined
  // todo: set delivery status on context as INCOMPLETE
  // }

  /**
   * Close off last interaction, push context status to complete, and write out exit timestamp.
   * Typically called internally.
   * @param ctx
   * @param completedAt
   */
  fun complete(context: Context, completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
    context.cursor = null
    context.delivery_status = DeliveryStatus.FINISHED_COMPLETE
    context.exit_at = createFormattedDate(completedAt)
  }

  /**
   * Seal up an [[IBlockInteraction]] with a timestamp once we've selected an exit.
   * @param intx
   * @param selectedExitId
   * @param completedAt
   */
  fun completeInteraction(
    intx: IBlockInteraction,
    selectedExitId: String,
    completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  ): IBlockInteraction {
    intx.exit_at = createFormattedDate(completedAt)
    intx.selected_exit_id = selectedExitId
    return intx
  }

  /**
   * Seal up an interaction with an [[IRunFlowBlock]] which spans multiple child [[IBlockInteraction]]s.
   * This will apply a timestamp to parent [[IRunFlowBlock]]'s [[IBlockInteraction]], unnest active flow one level
   * and return the interaction for that parent [[IRunFlowBlock]].
   * @param ctx
   * @param completedAt
   */
  fun completeActiveNestedFlow(completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)): IBlockInteraction {
    val nested_flow_block_interaction_id_stack = context.nested_flow_block_interaction_id_stack

    if (!context.isNested()) {
      throw ValidationException("Unable to complete a nested flow when not nested.")
    }

    val runFlowIntx = context.findInteractionWith(nested_flow_block_interaction_id_stack.last())

    // once we are in a valid state and able to find our corresponding interaction, let's update active nested flow
    nested_flow_block_interaction_id_stack.pop()

    // since we've un-nested one level, we may seek using freshly active flow
    val exit: IBlockExit = findFirstExitOnActiveFlowBlockFor(runFlowIntx)
    return completeInteraction(runFlowIntx, exit.uuid, completedAt)
  }

  /**
   * Take a richCursor down to the bare minimum for JSON-serializability.
   * interaction io.viamo.flow.runner."flow-spec".IBlockInteraction reduced to its UUID
   * prompt io.viamo.flow.runner.domain.prompt.IPrompt reduced to its raw config object.
   * Reverse of "hydrateRichCursorFrom()".
   * @param richCursor
   */
  fun dehydrateCursor(richCursor: IRichCursor): ICursor {
    return Cursor(
      interactionId = richCursor.interaction.uuid,
      promptConfig = richCursor.prompt?.config as IPromptConfig<*>?,
    )
  }

  /**
   * Take raw cursor off an "io.viamo.flow.runner."flow-spec".IContext" and generate a richer, more detailed version; typically not JSON-serializable.
   * interactionId String UUID becomes full io.viamo.flow.runner."flow-spec".IBlockInteraction data object
   * promptConfig io.viamo.flow.runner.domain.prompt.IPromptConfig becomes full-fledged Prompt instance corresponding to "kind".
   * Reverse of "dehydrateCursor()".
   * @param ctx
   */
  fun hydrateRichCursorFrom(): IRichCursor {
    return context.cursor?.let {
      val interaction = context.findInteractionWith(it.interactionId)
      val prompt = createPromptFrom(it.promptConfig, interaction)
      RichCursor(interaction, prompt)
    } ?: throw Exception("Expected context.cursor to be non null")
  }

  /**
   * Generate an io.viamo.flow.runner."flow-spec".IBlockInteraction, apply "postInteractionCreate()" hooks over it,
   * generate cursor with full-fledged prompt.
   * @param block
   * @param flowId
   * @param originFlowId
   * @param originBlockInteractionId
   */
  suspend fun initializeOneBlockInteraction(
    block: IBlock,
    flowId: String,
    originFlowId: String?,
    originBlockInteractionId: String?,
    createPrompt: suspend IBlockInteraction.() -> BasePrompt<*>?
  ): IRichCursor {
    var interaction = BlockInteraction.createBlockInteractionFor(block, flowId, originFlowId, originBlockInteractionId, idGenerator)
    interaction = behaviours.values.fold(interaction) { blockInteraction, behaviour -> (behaviour.postInteractionCreate(blockInteraction)) }

    return RichCursor(interaction, interaction.createPrompt())
  }

  /**
   * Type guard providing insight into whether or not prompt presence can be relied upon.
   * @param richCursor
   */
  fun isRichCursorInputRequired(richCursor: IRichCursor): Boolean {
    return richCursor.prompt != null
  }

  /**
   * Apply prompt value onto io.viamo.flow.runner."flow-spec".IBlockInteraction, complete io.viamo.flow.runner.domain.runners.IBlockRunner execution, mark prompt as having been submitted,
   * apply "postInteractionComplete()" hooks over it, and return io.viamo.flow.runner.domain.runners.IBlockRunner's selected exit.
   * @param richCursor
   * @param block
   */
  suspend fun runActiveBlockOn(richCursor: IRichCursor, block: IBlock): IBlockExit {
    val interaction = (richCursor).interaction
    requireNotNull(interaction) { "Unable to run with absent cursor interaction" }


    if (isRichCursorInputRequired(richCursor) && richCursor.prompt?.config?.isSubmitted == true) {
      throw ValidationException("Unable to run against previously processed prompt")
    }

    if (isRichCursorInputRequired(richCursor)) {
      interaction.value = richCursor.prompt?.value as String?
      interaction.has_response = interaction.value != null
    }

    val exit = createBlockRunnerFor(block).run(richCursor)

    completeInteraction(interaction, exit.uuid)

    if (isRichCursorInputRequired(richCursor)) {
      richCursor.prompt?.config?.isSubmitted = true
    }

    behaviours.values.forEach { b -> b.postInteractionComplete(richCursor.interaction) }

    return exit
  }

  /**
   * Produce an io.viamo.flow.runner.domain.runners.IBlockRunner instance leveraging "runnerFactoryStore" and "io.viamo.flow.runner."flow-spec".IBlock.type".
   * Raises when "ValidationException" when not found.
   * @param block
   * @param ctx
   */
  fun createBlockRunnerFor(block: IBlock): IBlockRunner<*> {
    val factory = runnerFactoryStore[block.getType()] ?: throw ValidationException("Unable to find factory for block type: ${block.getType()}")

    return factory(block, context)
  }

  /**
   * Initialize a block, close off any open past interaction, push newly initialized interaction onto history stack
   * and apply new cursor onto context.
   * @param block
   * @param ctx
   */
  override suspend fun navigateTo(block: IBlock): IRichCursor {
    val richCursor = _inflateInteractionAndContainerCursorFor(block)

    // todo: this could be findFirstExitOnActiveFlowBlockFor to an Expressions Behaviour
    cacheInteractionByBlockName(richCursor.interaction, block as IMessageBlock)

    return richCursor
  }

  fun _activateCursorOnto(richCursor: IRichCursor) {
    context.cursor = dehydrateCursor(richCursor)
  }

  private suspend fun _inflateInteractionAndContainerCursorFor(block: IBlock): IRichCursor {
    val originInteractionId = context.nested_flow_block_interaction_id_stack.last()

    val richCursor = initializeOneBlockInteraction(
      block,
      context.getActiveFlowId(),
      context.findInteractionWith(originInteractionId).flow_id,
      originInteractionId,
    ) { buildPromptFor(block, this) }

    context.interactions.add(richCursor.interaction)

    _activateCursorOnto(richCursor)

    return richCursor
  }

  /**
   * Stepping out is the act of moving back into parent flow.
   * However, we can't move up into parent flow without a cursor indicating we've moved.
   * "stepOut()" needs to be the things that discovers xb from xa (via nfbistack)
   * Then generating a cursor that indicates where we are.
   * ?? -> xa ->>> ya -> yb ->>> xb
   *
   * @note Does this push cursor into an out-of-sync state?
   *       Not when stepping out, because when stepping out, we're connecting previous RunFlow output
   *       to next block; when stepping IN, we need an explicit navigation to inject RunFlow in between
   *       the two Flows. */
  fun stepOut() = findNextBlockFrom(completeActiveNestedFlow())

  /**
   * Stepping into is the act of moving into a child flow.
   * However, we can't move into a child flow without a cursor indicating we've moved.
   * "stepInto()" needs to be the thing that discovers ya from xa (via first on flow in flows list)
   * Then generating a cursor that indicates where we are.
   * ?? -> xa ->>> ya -> yb ->>> xb
   *
   * todo: would it be possible for stepping into and out of be handled by the RunFlow itself?
   *       Eg. these are esentially RunFlowRunner's .start() + .resume() equivalents */
  fun stepInto(runFlowBlock: RunFlowBlock) = runFlowBlock.stepIntoAndGetNextBlock(this.context)

  fun findFirstExitOnActiveFlowBlockFor(blockInteraction: IBlockInteraction): IBlockExit {
    val exits = (context.findBlockOnActiveFlowWith(blockInteraction.block_id)).exits
    return exits.first()
  }

  /**
   * Find the active flow, then return first block on that flow if we've yet to initialize,
   * otherwise leverage current interaction's selected exit pointer.
   * @param ctx
   */
  fun findNextBlockOnActiveFlowFor(): IBlock? {
    val flow = context.getActiveFlow()
    val cursor = (context).cursor
        ?: return flow.blocks.first()

    val interaction = context.findInteractionWith(cursor.interactionId)
    return findNextBlockFrom(interaction)
  }

  /**
   * Find next block leveraging destinationBlock on current interaction's "selectedExit".
   * Raises when "selectedExitId" absent.
   * @param block_id
   * @param selectedExitId
   * @param ctx
   */
  fun findNextBlockFrom(blockInteraction: IBlockInteraction): IBlock {
    return blockInteraction.selected_exit_id?.let { selected_exit_id: String ->
      val block = context.findBlockOnActiveFlowWith(blockInteraction.block_id)
      val destination_block = (block.findBlockExitWith(selected_exit_id)).destination_block
      val blocks = (context.getActiveFlow()).blocks

      blocks.find { it.uuid == destination_block }
    } ?: throw ValidationException("Unable to navigate past incomplete interaction; did you forget to call runner.run()?")
  }

  suspend fun _inflatePromptForBlockOnto(
    richCursor: IRichCursor,
    block: IBlock,
    blockInteraction: IBlockInteraction = richCursor.interaction,
  ): BasePrompt<*>? {
    return buildPromptFor(block, blockInteraction)
  }

  /**
   * Build a prompt using block's corresponding "io.viamo.flow.runner.domain.runners.IBlockRunner.initialize()" configurator and promptKeyToPromptConstructorMap() to
   * discover prompt constructor.
   * @param block
   * @param interaction
   */
  override suspend fun buildPromptFor(block: IBlock, interaction: IBlockInteraction): BasePrompt<*>? {
    val promptConfig = createBlockRunnerFor(block).initialize(interaction)
    return createPromptFrom(promptConfig, interaction)
  }

  /**
   * New up prompt instance from an io.viamo.flow.runner.domain.prompt.IPromptConfig, assuming kind exists in "promptKeyToPromptConstructorMap()",
   * resulting in null when either config or interaction are absent.
   * @param config
   * @param interaction
   */
  fun createPromptFrom(config: IPromptConfig<*>?, interaction: IBlockInteraction?): BasePrompt<*>? {
    return if (config == null || interaction == null) {
      null
    } else {
      val promptConstructor = Prompt.valueOf(config.kind)?.promptConstructor
      promptConstructor?.new(config, interaction.uuid, this)
    }
  }
}
