package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.collections.Stack
import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.contact.Contact
import io.viamo.flow.runner.flowspec.contact.build
import io.viamo.flow.runner.flowspec.enums.DeliveryStatus
import io.viamo.flow.runner.flowspec.enums.DeliveryStatus.IN_PROGRESS
import io.viamo.flow.runner.flowspec.enums.SupportedMode
import io.viamo.flow.runner.flowspec.enums.SupportedMode.SMS
import io.viamo.flow.runner.flowspec.resource.Resource
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class ContextTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
     Context.buildNoNulls().let { original ->
       assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
     }
  }
}



fun Context.Companion.build(
  id: String = "1",
  created_at: Instant = createFormattedDate(),
  delivery_status: DeliveryStatus = IN_PROGRESS,
  mode: SupportedMode = SMS,
  language_id: String = "1",
  contact: Contact = Contact.build(),
  groups: List<Group> = emptyList(),
  session_vars: MutableMap<String, JsonElement> = mutableMapOf(),
  interactions: MutableList<BlockInteraction> = mutableListOf(),
  nested_flow_block_interaction_id_stack: Stack<String> = mutableListOf(),
  reversible_operations: MutableList<IReversibleUpdateOperation> = mutableListOf(),
  flows: List<Flow>,
  first_flow_id: String = flows.first().uuid,
  resources: List<Resource> = emptyList(),
  entry_at: Instant? = createFormattedDate(),
  exit_at: Instant? = null,
  user_id: String? = "1",
  org_id: String? = "1",
  vendor_metadata: JsonObject = buildJsonObject { },
  logs: JsonObject = buildJsonObject { },
) = Context(
  id = id,
  created_at = created_at,
  delivery_status = delivery_status,
  mode = mode,
  language_id = language_id,
  contact = contact,
  groups = groups,
  session_vars = session_vars,
  interactions = interactions,
  nested_flow_block_interaction_id_stack = nested_flow_block_interaction_id_stack,
  reversible_operations = reversible_operations,
  flows = flows,
  first_flow_id = first_flow_id,
  resources = resources,
  entry_at = entry_at,
  exit_at = exit_at,
  user_id = user_id,
  org_id = org_id,
  vendor_metadata = vendor_metadata,
  logs = logs,
)


fun Context.Companion.buildNoNulls(
  id: String = "1234",
  created_at: Instant = createFormattedDate(),
  delivery_status: DeliveryStatus = IN_PROGRESS,
  mode: SupportedMode = SMS,
  language_id: String = "language_id",
  contact: Contact = Contact.build(),
  groups: List<Group> = listOf(Group.buildNoNulls()),
  session_vars: MutableMap<String, JsonElement> = mutableMapOf(),
  interactions: MutableList<BlockInteraction> = mutableListOf(BlockInteraction.buildNoNulls()),
  nested_flow_block_interaction_id_stack: Stack<String> = mutableListOf(),
  reversible_operations: MutableList<IReversibleUpdateOperation> = mutableListOf(),
  flows: List<Flow> = listOf(Flow.buildNoNulls()),
  first_flow_id: String = "first_flow_id",
  resources: List<Resource> = listOf(Resource.buildNoNulls()),
  entry_at: Instant? = createFormattedDate(),
  exit_at: Instant? = createFormattedDate(),
  user_id: String? = "user_id",
  org_id: String? = "org_id",
  vendor_metadata: JsonObject = buildJsonObject { },
  logs: JsonObject = buildJsonObject { },
) = Context(
  id = id,
  created_at = created_at,
  delivery_status = delivery_status,
  mode = mode,
  language_id = language_id,
  contact = contact,
  groups = groups,
  session_vars = session_vars,
  interactions = interactions,
  nested_flow_block_interaction_id_stack = nested_flow_block_interaction_id_stack,
  reversible_operations = reversible_operations,
  flows = flows,
  first_flow_id = first_flow_id,
  resources = resources,
  entry_at = entry_at,
  exit_at = exit_at,
  user_id = user_id,
  org_id = org_id,
  vendor_metadata = vendor_metadata,
  logs = logs,
)
