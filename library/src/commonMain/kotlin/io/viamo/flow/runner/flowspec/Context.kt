package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.*
import io.viamo.flow.runner.flowspec.block.IBlock
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