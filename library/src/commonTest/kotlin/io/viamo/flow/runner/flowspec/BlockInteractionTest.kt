package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockInteractionTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    BlockInteraction.buildNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun BlockInteraction.Companion.buildNoNulls(
  uuid: String = "uuid",
  block_id: String = "block_id",
  flow_id: String = "flow_id",
  entry_at: Instant = createFormattedDate(),
  exit_at: Instant? =  createFormattedDate(),
  has_response: Boolean = true,
  value: String? = "value",
  details: JsonObject = buildJsonObject {  },
  selected_exit_id: String? = "selected_exit_id",
  type: String = "type",
  origin_block_interaction_id: String? = "origin_block_interaction_id",
  origin_flow_id: String = "origin_flow_id",
) = BlockInteraction(
  uuid = uuid,
  block_id = block_id,
  flow_id = flow_id,
  entry_at = entry_at,
  exit_at = exit_at,
  has_response = has_response,
  value = value,
  details = details,
  selected_exit_id = selected_exit_id,
  type = type,
  origin_block_interaction_id = origin_block_interaction_id,
  origin_flow_id = origin_flow_id,
)