package io.viamo.flow.runner.flowspec.block.type.message

import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.BlockUIMetadata
import io.viamo.flow.runner.flowspec.block.createNoNulls
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageBlockTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    MessageBlock.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}

fun MessageBlock.Companion.create(
  uuid: String,
  exits: List<BlockExit>,
  name: String = "name",
  label: String? = "label",
  semantic_label: String? = "semantic_label",
  tags: List<String>? = listOf(),
  vendor_metadata: JsonObject? = buildJsonObject {  },
  ui_metadata: BlockUIMetadata = BlockUIMetadata.createNoNulls(),
  config: MessageBlockConfig = MessageBlockConfig.createNoNulls(),
) = MessageBlock(
  uuid = uuid,
  name = name,
  label = label,
  semantic_label = semantic_label,
  tags = tags,
  vendor_metadata = vendor_metadata,
  ui_metadata = ui_metadata,
  exits = exits,
  config = config,
)

fun MessageBlock.Companion.createNoNulls(
  uuid: String = "uuid",
  name: String = "name",
  label: String? = "label",
  semantic_label: String? = "semantic_label",
  tags: List<String>? = listOf("a", "b", "c"),
  vendor_metadata: JsonObject? = JsonObject(emptyMap()),
  ui_metadata: BlockUIMetadata = BlockUIMetadata.createNoNulls(),
  exits: List<BlockExit> = listOf(BlockExit.createNoNulls()),
  config: MessageBlockConfig = MessageBlockConfig.createNoNulls(),
) = MessageBlock(
  uuid = uuid,
  name = name,
  label = label,
  semantic_label = semantic_label,
  tags = tags,
  vendor_metadata = vendor_metadata,
  ui_metadata = ui_metadata,
  exits = exits,
  config = config,
)