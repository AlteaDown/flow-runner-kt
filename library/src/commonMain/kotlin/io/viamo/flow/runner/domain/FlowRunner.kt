package io.viamo.flow.runner.domain

import Prompt
import Prompt.Companion.DEFAULT
import ValidationException
import io.viamo.flow.runner.collections.pop
import io.viamo.flow.runner.domain.behaviours.BacktrackingBehaviour.BasicBacktrackingBehaviour
import io.viamo.flow.runner.domain.behaviours.BehaviourConstructor
import io.viamo.flow.runner.domain.behaviours.IBehaviour
import io.viamo.flow.runner.domain.behaviours.IBehaviourConstructor
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.domain.runners.*
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.type
import io.viamo.flow.runner.flowspec.block.type.log.ILogBlock
import io.viamo.flow.runner.flowspec.block.type.log.LOG_TYPE
import io.viamo.flow.runner.flowspec.block.type.log.LogBlockRunner
import io.viamo.flow.runner.flowspec.block.type.message.IMessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock.Companion.MESSAGE_TYPE
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlockRunner
import io.viamo.flow.runner.flowspec.block.type.numeric.INumericResponseBlock
import io.viamo.flow.runner.flowspec.block.type.numeric.NUMERIC_TYPE
import io.viamo.flow.runner.flowspec.block.type.numeric.NumericResponseBlockRunner
import io.viamo.flow.runner.flowspec.block.type.open.IOpenResponseBlock
import io.viamo.flow.runner.flowspec.block.type.open.OPEN_TYPE
import io.viamo.flow.runner.flowspec.block.type.open.OpenResponseBlockRunner
import io.viamo.flow.runner.flowspec.block.type.output.IOutputBlock
import io.viamo.flow.runner.flowspec.block.type.output.OUTPUT_TYPE
import io.viamo.flow.runner.flowspec.block.type.output.OutputBlockRunner
import io.viamo.flow.runner.flowspec.block.type.print.IPrintBlock
import io.viamo.flow.runner.flowspec.block.type.print.PRINT_TYPE
import io.viamo.flow.runner.flowspec.block.type.print.PrintBlockRunner
import io.viamo.flow.runner.flowspec.block.type.run_flow.IRunFlowBlock
import io.viamo.flow.runner.flowspec.block.type.run_flow.RUN_FLOW_TYPE
import io.viamo.flow.runner.flowspec.block.type.run_flow.RunFlowBlock
import io.viamo.flow.runner.flowspec.block.type.run_flow.RunFlowBlockRunner
import io.viamo.flow.runner.flowspec.block.type.select_many.ISelectManyResponseBlock
import io.viamo.flow.runner.flowspec.block.type.select_many.SELECT_MANY_TYPE
import io.viamo.flow.runner.flowspec.block.type.select_many.SelectManyResponseBlockRunner
import io.viamo.flow.runner.flowspec.block.type.select_one.ISelectOneResponseBlock
import io.viamo.flow.runner.flowspec.block.type.select_one.SELECT_ONE_TYPE
import io.viamo.flow.runner.flowspec.block.type.select_one.SelectOneResponseBlockRunner
import io.viamo.flow.runner.flowspec.block.type.selectcase.CASE_TYPE
import io.viamo.flow.runner.flowspec.block.type.selectcase.CaseBlockRunner
import io.viamo.flow.runner.flowspec.block.type.selectcase.ICaseBlock
import io.viamo.flow.runner.flowspec.block.type.set_group_membership.ISetGroupMembershipBlock
import io.viamo.flow.runner.flowspec.block.type.set_group_membership.SET_GROUP_MEMBERSHIP_BLOCK_TYPE
import io.viamo.flow.runner.flowspec.block.type.set_group_membership.SetGroupMembershipBlockRunner
import io.viamo.flow.runner.flowspec.enums.DeliveryStatus
import io.viamo.flow.runner.flowspec.resource.Resource
import kotlinx.datetime.*

typealias BlockRunnerFactoryStore = Map<String, TBlockRunnerFactory>

interface IFlowNavigator {
  suspend fun navigateTo(context: Context, block: IBlock): Cursor.RichCursor
}

interface IPromptBuilder {
  suspend fun buildPromptFor(context: Context, block: IBlock, interaction: IBlockInteraction): BasePrompt<*>?
}

val DEFAULT_BEHAVIOUR_TYPES = listOf(
  BehaviourConstructor(name = "BasicBacktrackingBehaviour",
    new = { navigator, promptBuilder -> BasicBacktrackingBehaviour(navigator, promptBuilder) })
)

/**
 * Block types that do not request additional input from an "io.viamo.flow.runner."flow-spec".IContact"
 */
val NON_INTERACTIVE_BLOCK_TYPES = listOf(CASE_TYPE, RUN_FLOW_TYPE)

/**
 * A map of "io.viamo.flow.runner."flow-spec".IBlock.type" to an "TBlockRunnerFactory" function.
 */
fun createDefaultBlockRunnerStore(): Map<String, TBlockRunnerFactory> = mapOf(
  MESSAGE_TYPE to { block, context -> MessageBlockRunner(block as IMessageBlock, context) },
  OPEN_TYPE to { block, context -> OpenResponseBlockRunner(block as IOpenResponseBlock, context) },
  NUMERIC_TYPE to { block, context -> NumericResponseBlockRunner(block as INumericResponseBlock, context) },
  SELECT_ONE_TYPE to { block, context -> SelectOneResponseBlockRunner(block as ISelectOneResponseBlock, context) },
  SELECT_MANY_TYPE to { block, context -> SelectManyResponseBlockRunner(block as ISelectManyResponseBlock, context) },
  CASE_TYPE to { block, context -> CaseBlockRunner(block as ICaseBlock, context) },
  OUTPUT_TYPE to { block, context -> OutputBlockRunner(block as IOutputBlock, context) },
  LOG_TYPE to { block, context -> LogBlockRunner(block as ILogBlock, context) },
  PRINT_TYPE to { block, context -> PrintBlockRunner(block as IPrintBlock, context) },
  RUN_FLOW_TYPE to { block, context -> RunFlowBlockRunner(block as IRunFlowBlock, context) },
  SET_GROUP_MEMBERSHIP_BLOCK_TYPE to { block, context -> SetGroupMembershipBlockRunner(block as ISetGroupMembershipBlock, context) },
)

/**
 * Main interface into this library.
 * @see README.md for usage details.
 */
data class FlowRunner(
  /** Map of block types to a factory producting an io.viamo.flow.runner.domain.runners.IBlockRunner instnace. */
  override val runnerFactoryStore: Map<String, TBlockRunnerFactory> = createDefaultBlockRunnerStore(),
  /** Instance used to "generate()" unique IDs across interaction history. */
  val idGenerator: IIdGenerator = IdGeneratorUuidV4(),
  /** Instances providing isolated functionality beyond the default runner, leveraging built-in hooks. */
  val behaviours: MutableMap<String, IBehaviour> = mutableMapOf(),

  var customPrompts: List<Prompt<*>> = emptyList(),
  val resolveBlock: ((flowId: String, blockId: String) -> IBlock?) = { _, _ -> null },
  val resolveFlow: ((flowId: String) -> Flow?) = { null },
  val resolveResource: ((resourceId: String) -> Resource?) = { null },
  val resolveContact: ((contactId: String) -> Resource?) = { null },
) : IFlowRunner, IFlowNavigator, IPromptBuilder {

  init {
    initializeBehaviours(DEFAULT_BEHAVIOUR_TYPES)
    customPrompts = customPrompts + DEFAULT
  }

  /**
   * Take list of constructors and initialize them like: """
   * runner.initializeBehaviours([MyFirstBehaviour, MySecondBehaviour])
   * runner.behaviours.myFirst instanceof MyFirstBehaviour
   * runner.behaviours.mySecond instanceof MySecondBehaviour
   * """ */
  private fun initializeBehaviours(behaviourConstructors: List<IBehaviourConstructor>) {
    behaviourConstructors.forEach { behaviourConstructor ->
      behaviours[behaviourConstructor.getNameAsKey()] = behaviourConstructor.new(this, this)
    }
  }

  override suspend fun initializeContext(context: Context) = context.initialize(this)

  /**
   * Either begin or a resume a flow run, leveraging context instance member.
   */
  override suspend fun run(context: Context): Cursor? {
    if (!context.isInitialized()) {
      context.initialize(this)
    }

    return runUntilInputRequiredFrom(context)
  }

  /**
   * Initialize a block, close off any open past interaction, push newly initialized interaction onto history stack
   * and apply new cursor onto context.
   * @param block
   * @param ctx
   */
  override suspend fun navigateTo(context: Context, block: IBlock): Cursor.RichCursor =
      context._inflateInteractionAndContainerCursorFor(this, block)
        .also { cursor ->
          // todo: this could be findFirstExitOnActiveFlowBlockFor to an Expressions Behaviour
          context.cacheInteractionByBlockName(context, cursor.interaction, block as IMessageBlock)
        }

  /**
   * Pushes onward through the flow when cursor's prompt has been fulfilled and there are blocks to draw from.
   * This will continue running blocks until an interactive block is encountered and input is required from
   * the io.viamo.flow.runner."flow-spec".IContact.
   * Typically called internally.
   * @param ctx
   */
  suspend fun runUntilInputRequiredFrom(context: Context): Cursor? {

    do {
      if (context.isInputRequiredFor(this)) {
        println("Attempted to resume when prompt is not yet fulfilled; resurfacing same prompt instance.")
        return context.cursor
      }

      val uuid = context.cursor!!.findInteraction(context).block_id
      val activeBlock = context.findBlockOnActiveFlowWith(uuid = uuid)
      runActiveBlockOn(context, context.cursor!!, activeBlock)

      var block: IBlock? = context.findNextBlockOnActiveFlowFor(this)
      while (block == null && context.isNested()) {
        // nested flow complete, while more of parent flow remains
        block = stepOut(context)
      }

      if (block != null) {
        if (block.type == RUN_FLOW_TYPE) {
          check(block is RunFlowBlock)

          context.cursor = navigateTo(context, block)
          block = stepInto(context, block)
        }

        if (block != null) {
          context.cursor = navigateTo(context, block)
        }
      }
    } while (block != null)

    complete(context)
    return null
  }

  private fun maybeExitNestedFlow(context: Context, block: IBlock?): IBlock? {
    var block1 = block
    while (block1 == null && context.isNested()) {
      // nested flow complete, while more of parent flow remains
      block1 = stepOut(context)
    }
    return block1
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
    intx: IBlockInteraction, selectedExitId: String, completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
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
  fun completeActiveNestedFlow(
    context: Context,
    completedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  ): IBlockInteraction {
    val nestedFlowBlockInteractionIdStack = context.nested_flow_block_interaction_id_stack

    if (!context.isNested()) {
      throw ValidationException("Unable to complete a nested flow when not nested.")
    }

    val runFlowIntx = context.findInteractionWith(nestedFlowBlockInteractionIdStack.last())

    // once we are in a valid state and able to find our corresponding interaction, let's update active nested flow
    nestedFlowBlockInteractionIdStack.pop()

    // since we've un-nested one level, we may seek using freshly active flow
    val exit: IBlockExit = context.findFirstExitOnActiveFlowBlockFor(runFlowIntx)
    return completeInteraction(runFlowIntx, exit.uuid, completedAt)
  }

  /**
   * Take a richCursor down to the bare minimum for JSON-serializability.
   * interaction io.viamo.flow.runner."flow-spec".IBlockInteraction reduced to its UUID
   * prompt io.viamo.flow.runner.domain.prompt.IPrompt reduced to its raw config object.
   * Reverse of "hydrateRichCursorFrom()".
   * @param richCursor
   */
  fun dehydrateCursor(richCursor: Cursor.RichCursor): Cursor {
    return Cursor.BasicCursor(
      interactionId = richCursor.interaction.uuid,
      promptConfig = richCursor.prompt?.config,
    )
  }

  /**
   * Type guard providing insight into whether or not prompt presence can be relied upon.
   * @param richCursor
   */
  fun isRichCursorInputRequired(context: Context, richCursor: Cursor): Boolean {
    return richCursor.findPrompt(this, context) != null
  }

  /**
   * Apply prompt value onto io.viamo.flow.runner."flow-spec".IBlockInteraction, complete io.viamo.flow.runner.domain.runners.IBlockRunner execution, mark prompt as having been submitted,
   * apply "postInteractionComplete()" hooks over it, and return io.viamo.flow.runner.domain.runners.IBlockRunner's selected exit.
   * @param cursor
   * @param activeBlock
   */
  suspend fun runActiveBlockOn(context: Context, cursor: Cursor, activeBlock: IBlock): IBlockExit {
    check(!(isRichCursorInputRequired(context, cursor) && cursor.findPromptConfig()?.isSubmitted == true)) {
      """
        |Unable to run against previously processed prompt, ${isRichCursorInputRequired(context, cursor)}, 
        |${cursor.findPromptConfig()?.isSubmitted == true}""".trimMargin()
    }

    if (isRichCursorInputRequired(context, cursor)) {
      cursor.findInteraction(context).value = cursor.findPrompt(this, context)?.value as String?
      cursor.findInteraction(context).has_response = cursor.findInteraction(context).value != null
    }

    val exit = createBlockRunnerFor(context, activeBlock).run(cursor)
    completeInteraction(cursor.findInteraction(context), exit.uuid)

    if (isRichCursorInputRequired(context, cursor)) {
      cursor.findPromptConfig()?.isSubmitted = true
    }

    behaviours.values.forEach { behaviour -> behaviour.postInteractionComplete(context, cursor.findInteraction(context)) }

    return exit
  }

  /**
   * Produce an io.viamo.flow.runner.domain.runners.IBlockRunner instance leveraging "runnerFactoryStore" and "io.viamo.flow.runner."flow-spec".IBlock.type".
   * Raises when "ValidationException" when not found.
   * @param block
   * @param ctx
   */
  fun createBlockRunnerFor(context: Context, block: IBlock): IBlockRunner<*> = runnerFactoryStore[block.type]?.invoke(block, context)
      ?: throw ValidationException("Unable to find factory for block type: ${block.type}")

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
  fun stepOut(context: Context) = context.findNextBlockFrom(this, completeActiveNestedFlow(context))

  /**
   * Stepping into is the act of moving into a child flow.
   * However, we can't move into a child flow without a cursor indicating we've moved.
   * "stepInto()" needs to be the thing that discovers ya from xa (via first on flow in flows list)
   * Then generating a cursor that indicates where we are.
   * ?? -> xa ->>> ya -> yb ->>> xb
   *
   * todo: would it be possible for stepping into and out of be handled by the RunFlow itself?
   *       Eg. these are esentially RunFlowRunner's .start() + .resume() equivalents */
  fun stepInto(context: Context, runFlowBlock: RunFlowBlock) = runFlowBlock.stepIntoAndGetNextBlock(context)

  suspend fun _inflatePromptForBlockOnto(
    context: Context,
    richCursor: IRichCursor,
    block: IBlock,
    blockInteraction: IBlockInteraction = richCursor.interaction,
  ): BasePrompt<*>? {
    return buildPromptFor(context, block, blockInteraction)
  }

  /**
   * Build a prompt using block's corresponding "io.viamo.flow.runner.domain.runners.IBlockRunner.initialize()" configurator and promptKeyToPromptConstructorMap() to
   * discover prompt constructor.
   * @param block
   * @param interaction
   */
  override suspend fun buildPromptFor(context: Context, block: IBlock, interaction: IBlockInteraction): BasePrompt<*>? {
    return createPromptFrom(context, createBlockRunnerFor(context, block).initialize(interaction), interaction)
  }

  /**
   * New up prompt instance from an io.viamo.flow.runner.domain.prompt.IPromptConfig, assuming kind exists in "promptKeyToPromptConstructorMap()",
   * resulting in null when either config or interaction are absent.
   * @param config
   * @param interaction
   */
  fun createPromptFrom(context: Context, config: IPromptConfig<*>?, interaction: IBlockInteraction?): BasePrompt<*>? {
    return if (config == null || interaction == null) {
      null
    } else {
      val promptConstructor = customPrompts.firstOrNull { prompt -> prompt.key == config.kind }?.builder
      promptConstructor?.new(context, config, interaction.uuid, this)
    }
  }
}

interface IFlowRunner {
  val runnerFactoryStore: Map<String, TBlockRunnerFactory>

  suspend fun initializeContext(context: Context): IRichCursor?

  suspend fun run(context: Context): Cursor?
}

typealias TBlockRunnerFactory = (block: IBlock, ctx: Context) -> IBlockRunner<*>