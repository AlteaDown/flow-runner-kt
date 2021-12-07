package io.viamo.flow.runner.domain

import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.Flow
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.build
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.build
import io.viamo.flow.runner.flowspec.block.type.message.expectMessagePrompt
import io.viamo.flow.runner.flowspec.build
import kotlinx.coroutines.test.runTest
import kotlin.contracts.ExperimentalContracts
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertNull

@ExperimentalContracts
@kotlinx.coroutines.ExperimentalCoroutinesApi
class FlowRunnerTest {

  @Test
  @JsName("prompt_is_null_when_flow_ends")
  fun `cursor is null when flow ends`() = runTest {
    val flowRunner = FlowRunner(
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
    )

    val cursor: Cursor? = flowRunner.initialize()
      .findPrompt(flowRunner, flowRunner.context)
      .expectMessagePrompt { fulfill(value = "value") }

    assertNull(cursor, "Expected Cursor to be null")
  }
}