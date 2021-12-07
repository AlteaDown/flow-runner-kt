package io.viamo.flow.runner.domain

import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.build
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.build
import io.viamo.flow.runner.flowspec.block.type.message.expectMessagePrompt
import kotlinx.coroutines.test.runTest
import kotlin.contracts.ExperimentalContracts
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalContracts
@kotlinx.coroutines.ExperimentalCoroutinesApi
class FlowRunnerTest {

  @Test
  @JsName("prompt_is_null_when_flow_ends")
  fun `cursor is null when flow ends`() = runTest {
    val context = Context.build(
      first_flow_id = "1",
      interactions = mutableListOf(),
      flows = listOf(
        Flow.build(
          uuid = "1",
          first_block_id = "1",
          blocks = mutableListOf(
            MessageBlock.build(
              uuid = "1",
              exits = listOf(BlockExit.build())
            )
          )
        )
      )
    )
    val flowRunner = FlowRunner()

    val cursor: Cursor? = flowRunner.initializeContext(context)
      .findPrompt(flowRunner, context)
      .expectMessagePrompt { fulfill(value = "") }

    assertNull(cursor, "Expected Cursor to be null")
  }

  @Test
  @JsName("can_resume_from_middle_of_partially_completed_flow_with_minimum_resources")
  fun `can resume from middle of partially completed flow, with minimum resources`() = runTest {
    val context = Context.build(
      first_flow_id = "1",
      interactions = mutableListOf(
        BlockInteraction.buildMessageBlock("0", "0", "1", value = "", selected_exit_id = "1")
      ),
      flows = listOf(
        Flow.build(
          uuid = "1",
          first_block_id = "1",
          blocks = mutableListOf(
            MessageBlock.build(
              uuid = "2",
              exits = listOf(BlockExit.build())
            )
          )
        )
      )
    )
    val flowRunner = FlowRunner()

    val cursor: Cursor? = flowRunner.initializeContext(context)
      .findPrompt(flowRunner, context)
      .expectMessagePrompt {
        assertTrue { this.block?.uuid == "2" }
        fulfill(value = "value")
      }

    assertNull(cursor, "Expected Cursor to be null")
  }

  @Test
  @JsName("can_lazy_resolve_blocks")
  fun `can lazy resolve blocks`() = runTest {
    val context = Context.build(
      first_flow_id = "1",
      interactions = mutableListOf(
        BlockInteraction.buildMessageBlock("0", "0", "1", value = "", selected_exit_id = "1")
      ),
      flows = listOf(
        Flow.build(
          uuid = "1",
          first_block_id = "1",
          blocks = mutableListOf(
            MessageBlock.build(
              uuid = "1",
              exits = listOf(BlockExit.build(destination_block = "2"))
            )
          )
        )
      )
    )
    val flowRunner = FlowRunner(
      resolveBlock = { flowId, blockId ->
        MessageBlock.build(
          uuid = blockId,
          exits = listOf(BlockExit.build())
        )
      }
    )

    val cursor: Cursor? = flowRunner.initializeContext(context)
      .findPrompt(flowRunner, context)
      .expectMessagePrompt {
        assertTrue { this.block?.uuid == "1" }
        fulfill(value = "value")
      }!!
      .findPrompt(flowRunner, context)
      .expectMessagePrompt {
        assertTrue { this.block?.uuid == "2" }
        fulfill(value = "value")
      }

    assertNull(cursor, "Expected Cursor to be null")
  }
}