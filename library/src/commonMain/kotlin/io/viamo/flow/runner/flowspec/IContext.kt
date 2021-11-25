package io.viamo.flow.runner.flowspec

import ValidationException
import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.IIdGenerator
import io.viamo.flow.runner.domain.IdGeneratorUuidV4
import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.domain.prompt.IPrompt
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.model.block.IBlockConfig
import io.viamo.flow.runner.model.block.IRunFlowBlockConfig
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface ICursor {
  /**
   * UUID of the current interaction with a block.
   */
  val interactionId: String

  /**
   * A prompt configuration data object; optional, because not every block requests input from the io.viamo.flow.runner."flow-spec".Contact.
   * If it does, we call it an "io.viamo.flow.runner."flow-spec".ICursorInputRequired".
   * If not, "io.viamo.flow.runner."flow-spec".ICursorNoInputRequired" will have a "null-ish" "promptConfig".
   */
  val promptConfig: IPromptConfig<*>?
}

data class Cursor(
  override val interactionId: String,
  override val promptConfig: IPromptConfig<*>?,
) : ICursor

interface ICursorInputRequired : ICursor {
  override val interactionId: String
  override val promptConfig: IPromptConfig<*>
}

interface ICursorNoInputRequired : ICursor {
  override val interactionId: String
  override val promptConfig: IPromptConfig<*>?
}

interface IRichCursor<VALUE_TYPE, BLOCK_CONFIG_TYPE : IBlockConfig, PROMPT_CONFIG : IPromptConfig<VALUE_TYPE>> {
  /**
   * An object representation of the current interaction with a block.
   */
  val interaction: IBlockInteraction

  /**
   * In io.viamo.flow.runner.domain.prompt.IPrompt instance.
   * When present, we call it a TRichCursorInputRequired.
   * In absence, the TRichCursorNoInputRequired will maintain "prompt" with a null-ish value.
   */
  var prompt: IPrompt<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>?
}

data class RichCursor<VALUE_TYPE, BLOCK_CONFIG_TYPE, PROMPT_CONFIG>(
  override val interaction: IBlockInteraction,

  /**
   * In io.viamo.flow.runner.domain.prompt.IPrompt instance.
   * When present, we call it a TRichCursorInputRequired.
   * In absence, the TRichCursorNoInputRequired will maintain "prompt" with a null-ish value.
   */
  override val prompt: IPrompt<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>?,
) : IRichCursor<VALUE_TYPE, BLOCK_CONFIG_TYPE, PROMPT_CONFIG>
    where PROMPT_CONFIG : IPromptConfig<VALUE_TYPE>,
          BLOCK_CONFIG_TYPE : IBlockConfig

interface IRichCursorInputRequired<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>
    where PROMPT_CONFIG : IPromptConfig<VALUE_TYPE>,
          BLOCK_CONFIG_TYPE : IBlockConfig {
  val interaction: IBlockInteraction
  val prompt: IPrompt<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>
}

data class RichCursorInputRequired<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>(
  override val interaction: IBlockInteraction,
  override val prompt: IPrompt<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>,
) : IRichCursorInputRequired<PROMPT_CONFIG, VALUE_TYPE, BLOCK_CONFIG_TYPE>
    where PROMPT_CONFIG : IPromptConfig<VALUE_TYPE>,
          BLOCK_CONFIG_TYPE : IBlockConfig

interface IReversibleUpdateOperation {
  val interactionId: String?
  val forward: NonBreakingUpdateOperation
  val reverse: NonBreakingUpdateOperation
}

interface IContext {
  val id: String
  val created_at: String
  val entry_at: String?
  var exit_at: String?
  var delivery_status: DeliveryStatus
  val user_id: String?
  val org_id: String?
  val mode: SupportedMode
  val language_id: String
  val contact: IContact
  val groups: List<IGroup>
  val session_vars: MutableMap<String, JsonElement>
  val interactions: List<IBlockInteraction>
  val nested_flow_block_interaction_id_stack: Stack<String>
  val reversible_operations: List<IReversibleUpdateOperation>
  var cursor: ICursor?
  val flows: List<IFlow>
  val first_flow_id: String
  val resources: IResources
  val vendor_metadata: JsonObject
  val logs: JsonObject

  suspend fun createContextDataObjectFor(
    contact: IContact,
    groups: List<IGroup>,
    userId: String,
    orgId: String,
    flows: List<IFlow>,
    languageId: String,
    mode: SupportedMode = SupportedMode.OFFLINE,
    resources: List<IResource> = emptyList(),
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

  fun findInteractionWith(uuid: String): IBlockInteraction {
    return interactions.lastOrNull { it.uuid == uuid }
        ?: throw ValidationException("Unable to find interaction on context: $uuid in [${interactions.map { it.uuid }}]")
  }

  fun findFlowWith(uuid: String): IFlow {
    return flows.find { it.uuid == uuid }
        ?: throw ValidationException("Unable to find flow on context: $uuid in ${flows.map { it.uuid }}")
  }

  fun findBlockOnActiveFlowWith(uuid: String): IBlock<*> {
    return findBlockWith(uuid, getActiveFlowFrom())
  }

  fun findNestedFlowIdFor(interaction: IBlockInteraction): String {
    val flow = findFlowWith(interaction.flow_id)
    val runFlowBlock = findBlockWith(interaction.block_id, flow)
    return (runFlowBlock.config as IRunFlowBlockConfig).flow_id
  }

  fun getActiveFlowIdFrom(): String {
    return if (nested_flow_block_interaction_id_stack.isEmpty()) {
      first_flow_id
    } else {
      findNestedFlowIdFor(findInteractionWith(nested_flow_block_interaction_id_stack.last()))
    }
  }

  fun getActiveFlowFrom() = findFlowWith(getActiveFlowIdFrom())

  fun isLastBlockOn(block: IBlock<*>): Boolean {
    return !isNested() && isLastBlock(block)
  }

  fun isNested(): Boolean {
    return nested_flow_block_interaction_id_stack.isNotEmpty()
  }
}

interface IContextWithCursor : IContext {
  override val cursor: ICursor
}

interface IContextInputRequired : IContext {
  override val cursor: ICursorInputRequired
}