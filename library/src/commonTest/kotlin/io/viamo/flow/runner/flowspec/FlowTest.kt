package io.viamo.flow.runner.flowspec

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.ext.toJsonPrimitive
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.IBlock
import io.viamo.flow.runner.flowspec.block.create
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.create
import io.viamo.flow.runner.flowspec.block.type.message.createNoNulls
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    Flow.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun Flow.Companion.create(
  uuid: String = "1",
  name: String = "Flow Name",
  label: String? = "label",
  last_modified: Instant = createFormattedDate(),
  interaction_timeout: Int = 100,
  vendor_metadata: JsonObject = buildJsonObject {  },
  supported_modes: List<SupportedMode> = listOf(SupportedMode.OFFLINE),
  languages: List<Language> = listOf(Language.createNoNulls()),
  blocks: List<IBlock> = listOf(MessageBlock.create(uuid = "1", exits = listOf(BlockExit.create()))),
  first_block_id: String = "1",
  exit_block_id: String? = "exit_block_id",
) = Flow(
  uuid = uuid,
  name = name,
  label = label,
  last_modified = last_modified,
  interaction_timeout = interaction_timeout,
  vendor_metadata = vendor_metadata,
  supported_modes = supported_modes,
  languages = languages,
  blocks = blocks,
  first_block_id = first_block_id,
  exit_block_id = exit_block_id,
)

fun Flow.Companion.createNoNulls(
  uuid: String = "1",
  name: String = "name",
  label: String? = "label",
  last_modified: Instant = createFormattedDate(),
  interaction_timeout: Int = 1,
  vendor_metadata: JsonObject = JsonObject(mapOf("field" to "value".toJsonPrimitive())),
  supported_modes: List<SupportedMode> = listOf(
    SupportedMode.TEXT,
    SupportedMode.SMS,
    SupportedMode.USSD,
    SupportedMode.IVR,
    SupportedMode.RICH_MESSAGING,
    SupportedMode.OFFLINE,
  ),
  languages: List<Language> = listOf(Language.createNoNulls()),
  blocks: List<IBlock> = listOf(MessageBlock.createNoNulls()),
  first_block_id: String = "first_block_id",
  exit_block_id: String? = "exit_block_id",
) = Flow(
  uuid = uuid,
  name = name,
  label = label,
  last_modified = last_modified,
  interaction_timeout = interaction_timeout,
  vendor_metadata = vendor_metadata,
  supported_modes = supported_modes,
  languages = languages,
  blocks = blocks,
  first_block_id = first_block_id,
  exit_block_id = exit_block_id,
)