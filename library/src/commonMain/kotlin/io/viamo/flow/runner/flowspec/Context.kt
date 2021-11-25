package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.collections.Stack
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
  override val contact: IContact,
  override val groups: List<IGroup>,
  override val session_vars: MutableMap<String, JsonElement> = mutableMapOf(),
  override val interactions: MutableList<IBlockInteraction> = mutableListOf(),
  override val nested_flow_block_interaction_id_stack: Stack<String> = mutableListOf(),
  override val reversible_operations: MutableList<IReversibleUpdateOperation> = mutableListOf(),
  override val flows: List<IFlow>,
  override val first_flow_id: String,
  override val resources: IResources,
  override var entry_at: String? = null,
  override var exit_at: String? = null,
  override val user_id: String? = null,
  override val org_id: String? = null,
  override var cursor: ICursor? = null,
  override val vendor_metadata: JsonObject = JsonObject(emptyMap()),
  override val logs: JsonObject = JsonObject(emptyMap())
) : IContext
