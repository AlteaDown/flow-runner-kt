package io.viamo.flow.runner.flowspec

import Resource
import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.IIdGenerator
import io.viamo.flow.runner.domain.IdGeneratorUuidV4
import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.flowspec.contact.Contact
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Context(
  override val id: String,
  override val created_at: String,
  override var delivery_status: DeliveryStatus,
  override val mode: SupportedMode,
  override val language_id: String,
  override val contact: Contact,
  override val groups: List<Group>,
  override val session_vars: MutableMap<String, JsonElement> = mutableMapOf(),
  override val interactions: MutableList<IBlockInteraction> = mutableListOf(),
  override val nested_flow_block_interaction_id_stack: Stack<String> = mutableListOf(),
  override val reversible_operations: MutableList<IReversibleUpdateOperation> = mutableListOf(),
  override val flows: List<Flow>,
  override val first_flow_id: String,
  override val resources: List<Resource>,
  override var entry_at: String? = null,
  override var exit_at: String? = null,
  override val user_id: String? = null,
  override val org_id: String? = null,
  override var cursor: ICursor? = null,
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
