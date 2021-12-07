package io.viamo.flow.runner.flowspec.block.type.selectcase

import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit

/**
 * Block runner for "Core.Case" - Evaluates a list of expressions, one for each exit, and terminates through the first
 * exit where the corresponding expression evaluates to a "truthy" result.
 *
 * This block will sequentially evaluate the test expressions in each exit (passing over any default exit), in order.
 * If the test expression evaluates to a truthy value using the io.viamo.flow.runner."flow-spec".Context and Expressions framework, flow proceeds through
 * the corresponding exit (and no further exits are evaluated). If no test expressions are found truthy, the flow
 * proceeds through the default exit.
 */
class CaseBlockRunner(
  override val block: ICaseBlock,
  override val context: Context,
) : IBlockRunner<Nothing?> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null

  override suspend fun run(cursor: Cursor): IBlockExit {
    return try {
      block.setContactProperty(context)
      block.firstTrueOrNullBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      block.findDefaultBlockExitOrThrow()
    }
  }
}


