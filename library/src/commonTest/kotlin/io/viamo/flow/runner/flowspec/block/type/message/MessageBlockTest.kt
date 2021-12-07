package io.viamo.flow.runner.flowspec.block.type.message

import io.viamo.flow.runner.domain.FlowRunner
import io.viamo.flow.runner.ext.JSON
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.Flow
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.BlockUIMetadata
import io.viamo.flow.runner.flowspec.block.build
import io.viamo.flow.runner.flowspec.block.createNoNulls
import io.viamo.flow.runner.flowspec.build
import io.viamo.flow.runner.test.ISerializableTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
class MessageBlockTest : ISerializableTest {

  @Test
  override fun `is serializable to json then to object`() {
    MessageBlock.createNoNulls().let { original ->
      assertEquals(original, JSON.decodeFromString(JSON.encodeToString(original)))
    }
  }

  @Test
  @JsName("at_start_of_single_block_flow_message_block_does_not_skip")
  fun `at start of Single Block Flow message block does not skip`() = runTest {
    val cursor = FlowRunner(
      context = Context.build(
        first_flow_id = "1",
        interactions = mutableListOf(),
        flows = listOf(
          Flow.build(
            uuid = "1",
            first_block_id = "1",
            blocks = listOf(
              MessageBlock.build(
                uuid = "1",
                exits = listOf(BlockExit.build())
              )
            )
          )
        )
      )
    ).initialize()

    assertNotNull(cursor.prompt, "Expected Prompt to be not null")
  }
}

fun MessageBlock.Companion.build(
  uuid: String,
  exits: List<BlockExit>,
  name: String = "name",
  label: String? = "label",
  semantic_label: String? = "semantic_label",
  tags: List<String>? = listOf(),
  vendor_metadata: JsonObject? = buildJsonObject { },
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
