package io.viamo.flow.runner.block.type.message

import io.viamo.flow.runner.ISerializableTest
import io.viamo.flow.runner.block.BlockExit
import io.viamo.flow.runner.block.BlockUIMetadata
import io.viamo.flow.runner.block.create
import io.viamo.flow.runner.ext.JSON
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageBlockTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    MessageBlock.create().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }
}


fun MessageBlock.Companion.create() = MessageBlock(
  uuid = "uuid",
  name = "name",
  label = "label",
  semantic_label = "semantic_label",
  tags = listOf("a", "b", "c"),
  vendor_metadata = JsonObject(emptyMap()),
  ui_metadata = BlockUIMetadata.create(),
  exits = listOf(BlockExit.create()),
  config = MessageBlockConfig.create(),
)