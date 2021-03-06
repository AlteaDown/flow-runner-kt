package io.viamo.flow.runner.flowspec.block.type.print

import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.evaluateToString
import io.viamo.flow.runner.flowspec.block.type

/**
 * Block runner for "ConsoleIO\Print" - Prints a message to standard output, by evaluating an expression.
 */
class PrintBlockRunner(
  override val block: IPrintBlock,
  override val context: Context
) : IBlockRunner<Nothing?> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null

  override suspend fun run(cursor: Cursor): IBlockExit {
    return try {
      println(block.type + evaluateToString(block.config.message, context))
      block.setContactProperty(context)
      block.firstTrueOrNullBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      block.findDefaultBlockExitOrThrow()
    }
  }
}
