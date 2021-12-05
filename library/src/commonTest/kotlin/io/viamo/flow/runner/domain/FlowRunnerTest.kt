package io.viamo.flow.runner.domain

import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.Flow
import io.viamo.flow.runner.flowspec.block.BlockExit
import io.viamo.flow.runner.flowspec.block.create
import io.viamo.flow.runner.flowspec.block.type.message.MessageBlock
import io.viamo.flow.runner.flowspec.block.type.message.create
import io.viamo.flow.runner.flowspec.create
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertNotNull

@kotlinx.coroutines.ExperimentalCoroutinesApi
class FlowRunnerTest {
  @Test
  @JsName("at_start_of_flow_message_block_does_not_skip")
  fun `at start of Flow, message block does not skip`() = runTest {
    val maybePrompt = FlowRunner(
      context = Context.create(
        first_flow_id = "1",
        interactions = mutableListOf(),
        flows = listOf(
          Flow.create(
            uuid = "1",
            first_block_id = "1",
            blocks = listOf(
              MessageBlock.create(
                uuid = "1",
                exits = listOf(BlockExit.create())
              )
            )
          )
        )
      )
    ).initialize()

    assertNotNull(maybePrompt, "Expected Prompt to be not null")
  }

  @Test
  @JsName("prompt_is_null_when_flow_ends")
  fun `prompt is null when flow ends`() = runTest {
    val maybePrompt = FlowRunner(
      context = Context.create(
        first_flow_id = "1",
        interactions = mutableListOf(),
        flows = listOf(
          Flow.create(
            uuid = "1",
            first_block_id = "1",
            blocks = listOf(
              MessageBlock.create(
                uuid = "1",
                exits = listOf(BlockExit.create())
              )
            )
          )
        )
      )
    ).initialize()

    assertNotNull(maybePrompt, "Expected Prompt to be not null")
  }
}