package io.viamo.flow.runner.domain

import OpenResponseBlockRunner
import OutputBlockRunner
import PrintBlockRunner
import Prompt
import RunFlowBlockRunner
import SelectManyResponseBlockRunner
import SelectOneResponseBlockRunner
import SetGroupMembershipBlockRunner
import ValidationException
import io.viamo.flow.runner.collections.pop
import io.viamo.flow.runner.collections.push
import io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.BasicBacktrackingBehaviour
import io.viamo.flow.runner.domain.behaviours.BehaviourConstructor
import io.viamo.flow.runner.domain.behaviours.IBehaviour
import io.viamo.flow.runner.domain.behaviours.IBehaviourConstructor
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.domain.runners.*
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.*
import kotlinx.datetime.*
import kotlinx.serialization.json.JsonObject

typealias BlockRunnerFactoryStore = Map<String, TBlockRunnerFactory>

interface IFlowNavigator {
  fun navigateTo(block: IBlock<*>, ctx: IContext): IRichCursorInputRequired<*, *, *>
}

interface IPromptBuilder {
  suspend fun <T> buildPromptFor(block: IBlock<*>, interaction: IBlockInteraction): BasePrompt<out T, IPromptConfig<out T>>?
}

val DEFAULT_BEHAVIOUR_TYPES: List<IBehaviourConstructor> = listOf(
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
fun createDefaultBlockRunnerStore(): IBlockRunnerFactoryStore = mapOf(
  "MobilePrimitives.Message" to { iBlock, iContext -> MessageBlockRunner(iBlock as IMessageBlock, iContext) },
  "MobilePrimitives.OpenResponse" to { iBlock, iContext -> OpenResponseBlockRunner(iBlock as IOpenResponseBlock, iContext) },
  "MobilePrimitives.NumericResponse" to { iBlock, iContext -> NumericResponseBlockRunner(iBlock as INumericResponseBlock, iContext) },
  "MobilePrimitives.SelectOneResponse" to { iBlock, iContext -> SelectOneResponseBlockRunner(iBlock as ISelectOneResponseBlock, iContext) },
  "MobilePrimitives.SelectManyResponse" to { iBlock, iContext ->
    SelectManyResponseBlockRunner(
      iBlock as ISelectManyResponseBlock,
      iContext
    )
  },
  "Core.Case" to { iBlock, iContext -> CaseBlockRunner(iBlock as ICaseBlock, iContext) },
  "Core.Output" to { iBlock, iContext -> OutputBlockRunner(iBlock as IOutputBlock, iContext) },
  "Core.Log" to { iBlock, iContext -> LogBlockRunner(iBlock as ILogBlock, iContext) },
  "ConsoleIO.Print" to { iBlock, iContext -> PrintBlockRunner(iBlock as IPrintBlock, iContext) },
  "Core.RunFlow" to { iBlock, iContext -> RunFlowBlockRunner(iBlock as IRunFlowBlock, iContext) },
  SET_GROUP_MEMBERSHIP_BLOCK_TYPE to { iBlock, iContext -> SetGroupMembershipBlockRunner(iBlock as ISetGroupMembershipBlock, iContext) },
)

/**
 * Main interface into this library.
 * @see README.md for usage details.
 */
data class FlowRunner(
  /** Running context, JSON-serializable entity with enough information to start or resume a Flow. */
  override var context: Context,
  /** Map of block types to a factory producting an io.viamo.flow.runner.domain.runners.IBlockRunner instnace. */
  override val runnerFactoryStore: IBlockRunnerFactoryStore = createDefaultBlockRunnerStore(),
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
  override suspend fun initialize(): IRichCursor<*, *, *>? {
    val block = findNextBlockOnActiveFlowFor() ?: throw ValidationException("Unable to initialize flow without blocks.")

    context.delivery_status = DeliveryStatus.IN_PROGRESS
    context.entry_at = createFormattedDate()

    // kick-start by navigating to first block
    return navigateTo(block, context)
  }

  /**
   * Verify whether or not we have a pointer in interaction history or not.
   * This identifies whether or not a run is in progress.
   * @param ctx
   */
  fun isInitialized(ctx: IContext) = context.cursor != null

  /**
   * Decipher whether or not cursor points to the first interactive block or not.
   */
  fun isFirst(): Boolean {
    if (!isInitialized(context)) {
      return true
    }

    return context.interactions.find { !NON_INTERACTIVE_BLOCK_TYPES.contains(it.type) }
      ?.let { it.uuid == context.cursor?.interactionId }
        ?: true
  }

  /**
   * Decipher whether or not cursor points to the last block from interaction history.
   */
  fun isLast(): Boolean {
    return if (isInitialized(context)) {
      context.interactions.lastOrNull()?.uuid == context.cursor?.interactionId
    } else {
      true
    }
  }

  /**
   * Either begin or a resume a flow run, leveraging context instance member.
   */
  override suspend fun run(): IRichCursorInputRequired<*, *, *>? {
    if (!isInitialized(context)) {
      initialize()
    }

    return runUntilInputRequiredFrom(context as IContextWithCursor)
  }

  /**
   * Decipher whether or not calling run() will be able to proceed or our cursor's prompt is in an invalid state.
   * @param ctx
   */
  fun isInputRequiredFor(): Boolean {
    if (context.cursor?.promptConfig == null) {
      return false
    }

    if (context.cursor?.promptConfig?.value == null) {
      return true
    }

    val prompt = (this.hydrateRichCursorFrom() as IRichCursorInputRequired<*, *, *>).prompt

    try {
      prompt.validate(prompt.value)
      return false
    } catch (e) {
      return true
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
  //TODO: Implement backtracking
  //fun applyReversibleDataOperation(
  //forward: NonBreakingUpdateOperation,
  //reverse: NonBreakingUpdateOperation,
  //context: IContext = this.context
  //): Unit {
  //  context.session_vars = update(context.session_vars, forward)
  //  context.reversible_operations.push({
  //    interactionId: last(context.interactions)?.uuid,
  //    forward,
  //    reverse,
  //  })
  //}

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
  suspend fun runUntilInputRequiredFrom(ctx: IContextWithCursor): IRichCursorInputRequired<*, *, *>? {
    var richCursor: IRichCursor<*, *, *> = hydrateRichCursorFrom()
    var block: IBlock<*>? = this.context.findBlockOnActiveFlowWith(richCursor.interaction.block_id)

    do {
      if (isInputRequiredFor()) {
        println("Attempted to resume when prompt is not yet fulfilled; resurfacing same prompt instance.")
        return richCursor as IRichCursorInputRequired<*, *, *>
      }

      runActiveBlockOn(richCursor, block)

      block = findNextBlockOnActiveFlowFor()

      while (block == null && this.context.isNested()) {
        // nested flow complete, while more of parent flow remains
        block = stepOut()
      }

      if (block == null) {
        // bail-- we're done.
        continue
      }

      if (block.type == "Core.RunFlow") {
        richCursor = navigateTo(block, context)
        block = stepInto(block)
      }

      if (block == null) {
        // bail-- we done.
        continue
      }

      richCursor = navigateTo(block, context)
    } while (block != null)

    complete(context)
    return
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
  fun complete(context: IContext, completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
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
  fun dehydrateCursor(richCursor: IRichCursor<*, *, *>): ICursor {
    return Cursor(
      interactionId = richCursor.interaction.uuid,
      promptConfig = richCursor.prompt?.config,
    )
  }

  /**
   * Take raw cursor off an "io.viamo.flow.runner."flow-spec".IContext" and generate a richer, more detailed version; typically not JSON-serializable.
   * interactionId String UUID becomes full io.viamo.flow.runner."flow-spec".IBlockInteraction data object
   * promptConfig io.viamo.flow.runner.domain.prompt.IPromptConfig becomes full-fledged Prompt instance corresponding to "kind".
   * Reverse of "dehydrateCursor()".
   * @param ctx
   */
  fun hydrateRichCursorFrom(): IRichCursor<*, *, *> {
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
  suspend fun initializeOneBlock(
    block: IBlock<*>,
    flowId: String,
    originFlowId: String?,
    originBlockInteractionId: String?
  ): RichCursor<Nothing?, IBlockConfig, IPromptConfig<Nothing?>> {
    var interaction = createBlockInteractionFor(block, flowId, originFlowId, originBlockInteractionId)

    interaction = behaviours.values.fold(interaction) { blockInteraction, behaviour -> (behaviour.postInteractionCreate(blockInteraction)) }

    return RichCursor<Nothing?, IBlockConfig, IPromptConfig<Nothing?>>(interaction, null)
  }

  /**
   * Type guard providing insight into whether or not prompt presence can be relied upon.
   * @param richCursor
   */
  fun isRichCursorInputRequired(richCursor: IRichCursor<*, *, *>): Boolean {
    return richCursor.prompt != null
  }

  /**
   * Apply prompt value onto io.viamo.flow.runner."flow-spec".IBlockInteraction, complete io.viamo.flow.runner.domain.runners.IBlockRunner execution, mark prompt as having been submitted,
   * apply "postInteractionComplete()" hooks over it, and return io.viamo.flow.runner.domain.runners.IBlockRunner's selected exit.
   * @param richCursor
   * @param block
   */
  suspend fun runActiveBlockOn(richCursor: IRichCursor<*, *, *>, block: IBlock<*>?): IBlockExit {
    val interaction = (richCursor).interaction
    requireNotNull(interaction) { "Unable to run with absent cursor interaction" }


    if (isRichCursorInputRequired(richCursor) && richCursor.prompt.config.isSubmitted) {
      throw ValidationException("Unable to run against previously processed prompt")
    }

    if (isRichCursorInputRequired(richCursor)) {
      interaction.value = richCursor.prompt.value
      interaction.has_response = interaction.value != null
    }

    val exit = createBlockRunnerFor(block, context).run(richCursor)

    completeInteraction(interaction, exit.uuid)

    if (isRichCursorInputRequired(richCursor)) {
      richCursor.prompt.config.isSubmitted = true
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
  fun createBlockRunnerFor(block: IBlock<*>, ctx: IContext): IBlockRunner<*, *, *> {
    val factory = runnerFactoryStore[block.type] ?: throw ValidationException("Unable to find factory for block type: ${block.type}")

    return factory(block, context)
  }

  /**
   * Initialize a block, close off any open past interaction, push newly initialized interaction onto history stack
   * and apply new cursor onto context.
   * @param block
   * @param ctx
   */
  suspend fun navigateTo(block: IBlock<*>, ctx: IContext): IRichCursor<*, *, *> {
    val richCursor = _inflateInteractionAndContainerCursorFor(block)

    _activateCursorOnto(richCursor)

    _inflatePromptForBlockOnto(richCursor, block)

    // todo: this could be findFirstExitOnActiveFlowBlockFor to an Expressions Behaviour
    cacheInteractionByBlockName(richCursor.interaction, block as IMessageBlock)

    return richCursor
  }

  fun _activateCursorOnto(richCursor: IRichCursor<*, *, *>) {
    context.cursor = dehydrateCursor(richCursor)
  }

  private suspend fun _inflateInteractionAndContainerCursorFor(block: IBlock<*>): IRichCursor<*, *, *> {
    val originInteractionId = context.nested_flow_block_interaction_id_stack.last()

    val richCursor = initializeOneBlock(
      block,
      context.getActiveFlowIdFrom(),
      context.findInteractionWith(originInteractionId).flow_id,
      originInteractionId
    )

    context.interactions.add(richCursor.interaction)

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
  fun stepInto(runFlowBlock: IBlock<*>): IBlock<*>? {
    if (runFlowBlock.type != "Core.RunFlow") {
      throw ValidationException("Unable to step into a non-Core.RunFlow block type")
    }

    val runFlowInteraction = context.interactions.lastOrNull()
        ?: throw ValidationException("Unable to step into Core.RunFlow that hasn't yet been started")

    if (runFlowBlock.uuid != runFlowInteraction.block_id) {
      throw ValidationException("Unable to step into Core.RunFlow block that doesn't match last interaction")
    }

    context.nested_flow_block_interaction_id_stack.push(runFlowInteraction.uuid)

    val firstNestedBlock = context.getActiveFlowFrom().blocks.first()
    // todo: use io.viamo.flow.runner."flow-spec".IFlow.firstBlockId
    return firstNestedBlock
  }

  fun findFirstExitOnActiveFlowBlockFor(blockInteraction: IBlockInteraction): IBlockExit {
    val exits = (context.findBlockOnActiveFlowWith(blockInteraction.block_id)).exits
    return exits.first()
  }

  /**
   * Find the active flow, then return first block on that flow if we've yet to initialize,
   * otherwise leverage current interaction's selected exit pointer.
   * @param ctx
   */
  fun findNextBlockOnActiveFlowFor(): IBlock<*>? {
    val flow = context.getActiveFlowFrom()
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
  fun findNextBlockFrom(blockInteraction: IBlockInteraction): IBlock<*> {
    return blockInteraction.selected_exit_id?.let { selected_exit_id: String ->
      val block = context.findBlockOnActiveFlowWith(blockInteraction.block_id)
      val destination_block = (findBlockExitWith(selected_exit_id, block)).destination_block
      val blocks = (context.getActiveFlowFrom()).blocks

      blocks.find { it.uuid == destination_block }
    } ?: throw ValidationException("Unable to navigate past incomplete interaction; did you forget to call runner.run()?")
  }

  /**
   * Generate a concrete "io.viamo.flow.runner."flow-spec".IBlockInteraction" data object, pre-populated with:
   * - UUID via "io.viamo.flow.runner.domain.IIdGenerator.generate()"
   * - entryAt via current timestamp
   * - flowId (provisioned)
   * - block_id via block.uuid
   * - type via block.type provisioned
   * - hasResponse as "false"
   * @param block_id
   * @param type
   * @param flowId
   * @param originFlowId
   * @param originBlockInteractionId
   */
  private suspend fun createBlockInteractionFor(
    block: IBlock<*>,
    flowId: String,
    originFlowId: String?,
    originBlockInteractionId: String?
  ): IBlockInteraction {
    return BlockInteraction(
      uuid = idGenerator.generate(),
      block_id = block.uuid,
      flow_id = flowId,
      entry_at = createFormattedDate(),
      exit_at = null,
      has_response = false,
      value = null,
      selected_exit_id = null,
      origin_flow_id = originFlowId,
      origin_block_interaction_id = originBlockInteractionId,
      type = block.type,
      details = mapOf()
    )
  }

  suspend fun _inflatePromptForBlockOnto(richCursor: IRichCursor<*, *, *>, block: IBlock<*>) {
    richCursor.prompt = buildPromptFor(block, richCursor.interaction)
    context.cursor.promptConfig = richCursor.prompt?.config
  }

  /**
   * Build a prompt using block's corresponding "io.viamo.flow.runner.domain.runners.IBlockRunner.initialize()" configurator and promptKeyToPromptConstructorMap() to
   * discover prompt constructor.
   * @param block
   * @param interaction
   */
  override suspend fun <T> buildPromptFor(block: IBlock<*>, interaction: IBlockInteraction): BasePrompt<T, *>? {
    val promptConfig = createBlockRunnerFor(block, context).initialize(interaction)
    return createPromptFrom(promptConfig, interaction)
  }

  /**
   * New up prompt instance from an io.viamo.flow.runner.domain.prompt.IPromptConfig, assuming kind exists in "promptKeyToPromptConstructorMap()",
   * resulting in null when either config or interaction are absent.
   * @param config
   * @param interaction
   */
  fun <T> createPromptFrom(config: IPromptConfig<T>?, interaction: IBlockInteraction?): BasePrompt<T, *>? {
    return if (config == null || interaction == null) {
      null
    } else {
      val promptConstructor = Prompt.valueOf<T>(config.kind)?.promptConstructor
      promptConstructor?.new(config, interaction.uuid, this)
    }
  }
}

