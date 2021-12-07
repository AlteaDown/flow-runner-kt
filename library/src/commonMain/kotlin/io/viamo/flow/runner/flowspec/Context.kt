package io.viamo.flow.runner.flowspec

import ValidationException
import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.*
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.type.message.IMessageBlock
import io.viamo.flow.runner.flowspec.block.type.run_flow.IRunFlowBlockConfig
import io.viamo.flow.runner.flowspec.contact.Contact
import io.viamo.flow.runner.flowspec.contact.IContact
import io.viamo.flow.runner.flowspec.enums.DeliveryStatus
import io.viamo.flow.runner.flowspec.enums.SupportedMode
import io.viamo.flow.runner.flowspec.resource.IResource
import io.viamo.flow.runner.flowspec.resource.Resource
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Context(
  override val id: String,
  override val created_at: Instant,
  override var delivery_status: DeliveryStatus,
  override val mode: SupportedMode,
  override val language_id: String,
  override val contact: Contact,
  override val groups: List<Group>,
  override val session_vars: MutableMap<String, JsonElement> = mutableMapOf(),
  override val interactions: MutableList<BlockInteraction> = mutableListOf(),
  override val nested_flow_block_interaction_id_stack: Stack<String> = mutableListOf(),
  override val reversible_operations: MutableList<IReversibleUpdateOperation> = mutableListOf(),
  override val flows: List<Flow>,
  override val first_flow_id: String,
  override val resources: List<Resource>,
  override var entry_at: Instant? = null,
  override var exit_at: Instant? = null,
  override val user_id: String? = null,
  override val org_id: String? = null,
  @Serializable(with = CursorSerializer::class)
  var cursor: Cursor? = null,
  override val vendor_metadata: JsonObject = JsonObject(emptyMap()),
  override val logs: JsonObject = JsonObject(emptyMap())
) : IContext {

  suspend fun createContextDataObjectFor(
    contact: Contact,
    groups: List<Group>,
    userId: String,
    orgId: String,
    flows: List<Flow>,
    languageId: String,
    mode: SupportedMode = SupportedMode.OFFLINE,
    resources: List<Resource> = emptyList(),
    idGenerator: IIdGenerator = IdGeneratorUuidV4()
  ): IContext {
    return Context(
      id = idGenerator.generate(),
      created_at = createFormattedDate(),
      delivery_status = DeliveryStatus.QUEUED,
      user_id = userId,
      org_id = orgId,
      mode = mode,
      language_id = languageId,
      contact = contact,
      groups = groups,
      flows = flows,
      first_flow_id = flows[0].uuid,
      resources = resources,
    )
  }

  /**
   * Initialize entry point into this flow run; typically called internally.
   * Sets up first block, engages run state and entry timestamp on context.
   */
  suspend fun initialize(flowRunner: FlowRunner): Cursor.RichCursor {
    val block = findNextBlockOnActiveFlowFor() ?: throw ValidationException("Unable to initialize flow without blocks.")

    delivery_status = DeliveryStatus.IN_PROGRESS
    entry_at = createFormattedDate()

    // kick-start by navigating to first block
    return flowRunner.navigateTo(this, block)
  }

  /**
   * Verify whether or not we have a pointer in interaction history or not.
   * This identifies whether or not a run is in progress.
   * @param ctx
   */
  fun isInitialized() = cursor != null

  /**
   * Decipher whether or not calling run() will be able to proceed or our cursor's prompt is in an invalid state.
   * @param ctx
   */
  fun isInputRequiredFor(flowRunner: FlowRunner): Boolean {
    return cursor?.let { cursor ->
      val promptConfig = cursor.findPromptConfig()
      when {
        // at end of FlowSession
        promptConfig == null -> false
        promptConfig.value == null && promptConfig.isResponseRequired -> true
        else -> try {
          val prompt = cursor.findPrompt(flowRunner, this)
          prompt?.validate(prompt.value)
          false
        } catch (e: Exception) {
          e.printStackTrace()
          true
        }
      }
    } ?: false
  }

  // todo: this could be findFirstExitOnActiveFlowBlockFor to an Expressions Behaviour
  //       ie. cacheInteractionByBlockName, applyReversibleDataOperation and reverseLastDataOperation
  fun cacheInteractionByBlockName(
    context: Context,
    blockInteraction: IBlockInteraction, block: IMessageBlock
  ) {
    val uuid = blockInteraction.uuid
    val entry_at = blockInteraction.entry_at
    val name = block.name
    val prompt = block.config.prompt

    if ("block_interactions_by_block_name" !in context.session_vars) {
      context.session_vars.put("block_interactions_by_block_name", JsonObject(emptyMap()))
    }

    // create a cache of "{[block.name]: {...}}" for subsequent lookups
    val blockNameKey = "block_interactions_by_block_name.${name}"
    val previous = context.session_vars[blockNameKey] // TODO: Implement backtracking
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
  fun applyReversibleDataOperation(
    forward: Any,
    reverse: Any,
  ): Unit { //TODO: Implement backtracking
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
   */ // TODO: Implement backtracking
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
   * Find the active flow, then return first block on that flow if we've yet to initialize,
   * otherwise leverage current interaction's selected exit pointer.
   * @param ctx
   */
  fun findNextBlockOnActiveFlowFor(): IBlock? {
    return if (cursor == null) {
      getActiveFlow().blocks.first()
    } else {
      cursor?.let { findNextBlockFrom(it.findInteraction(this)) }
    }
  }

  fun findFirstExitOnActiveFlowBlockFor(blockInteraction: IBlockInteraction): IBlockExit {
    val exits = (findBlockOnActiveFlowWith(blockInteraction.block_id)).exits
    return exits.first()
  }

  /**
   * Find next block leveraging destinationBlock on current interaction's "selectedExit".
   * Raises when "selectedExitId" absent.
   * @param block_id
   * @param selectedExitId
   * @param ctx
   */
  fun findNextBlockFrom(blockInteraction: IBlockInteraction): IBlock? {
    val selectedExitId = blockInteraction.selected_exit_id
    checkNotNull(selectedExitId) { "Unable to navigate past incomplete interaction; did you forget to call runner.run()?" }

    val destinationBlock = getDestinationBlock(blockInteraction, selectedExitId)
    return getActiveFlow().blocks.find { it.uuid == destinationBlock }
  }

  private fun getDestinationBlock(blockInteraction: IBlockInteraction, selected_exit_id: String): String? {
    return findBlockOnActiveFlowWith(blockInteraction.block_id)
      .findBlockExitWith(selected_exit_id).destination_block
  }

  suspend fun _inflateInteractionAndContainerCursorFor(flowRunner: FlowRunner, block: IBlock): Cursor.RichCursor {
    val originInteractionId = nested_flow_block_interaction_id_stack.lastOrNull()

    val richCursor = initializeOneBlockInteraction(
      flowRunner,
      block,
      getActiveFlowId(),
      originInteractionId?.let { findInteractionWith(originInteractionId).flow_id },
      originInteractionId,
    ) { flowRunner.buildPromptFor(this@Context, block, this) }

    interactions.add(richCursor.interaction)
    cursor = richCursor

    return richCursor
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
    flowRunner: FlowRunner,
    block: IBlock,
    flowId: String,
    originFlowId: String?,
    originBlockInteractionId: String?,
    createPrompt: suspend IBlockInteraction.() -> BasePrompt<*>?
  ): Cursor.RichCursor {
    var interaction =
        BlockInteraction.createBlockInteractionFor(block, flowId, originFlowId, originBlockInteractionId, flowRunner.idGenerator)
    interaction = flowRunner.behaviours.values.fold(interaction) { blockInteraction, behaviour ->
      (behaviour.postInteractionCreate(
        this,
        blockInteraction
      ))
    }

    return Cursor.RichCursor(interaction, interaction.createPrompt())
  }
}

interface IReversibleUpdateOperation {
  val interactionId: String?
  val forward:/* TODO Was NonBreakingUpdateOperation */ Any
  val reverse: /* TODO Was NonBreakingUpdateOperation */ Any
}

interface IContext {
  val id: String
  val created_at: Instant
  val entry_at: Instant?
  var exit_at: Instant?
  var delivery_status: DeliveryStatus
  val user_id: String?
  val org_id: String?
  val mode: SupportedMode
  val language_id: String
  val contact: IContact
  val groups: List<IGroup>
  val session_vars: MutableMap<String, JsonElement>
  val interactions: List<BlockInteraction>
  val nested_flow_block_interaction_id_stack: Stack<String>
  val reversible_operations: List<IReversibleUpdateOperation>
  val flows: List<IFlow>
  val first_flow_id: String
  val resources: List<IResource>
  val vendor_metadata: JsonObject
  val logs: JsonObject

  fun getActiveFlow() = findFlowWith(getActiveFlowId())
  fun isLastBlockOn(block: IBlock) = !isNested() && block.isLastInFlow()
  fun isNested() = nested_flow_block_interaction_id_stack.isNotEmpty()
  fun findInteractionWith(uuid: String) = interactions.last { it.uuid == uuid }
  fun findFlowWith(uuid: String) = flows.first { it.uuid == uuid }
  fun findBlockOnActiveFlowWith(uuid: String) = getActiveFlow().findBlockWith(uuid)
  fun findNestedFlowIdFor(interaction: IBlockInteraction) =
      (findFlowWith(interaction.flow_id).findBlockWith(interaction.block_id).config as IRunFlowBlockConfig).flow_id

  fun getActiveFlowId() = if (nested_flow_block_interaction_id_stack.isEmpty()) {
    first_flow_id
  } else {
    findNestedFlowIdFor(findInteractionWith(nested_flow_block_interaction_id_stack.last()))
  }
}