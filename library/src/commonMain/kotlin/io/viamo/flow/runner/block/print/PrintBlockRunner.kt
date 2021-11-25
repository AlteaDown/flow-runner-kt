package io.viamo.flow.runner.block.print

import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*

/**
 * Block runner for "ConsoleIO\Print" - Prints a message to standard output, by evaluating an expression.
 */
class PrintBlockRunner(
  override val block: IPrintBlock,
  override val context: IContext
) : IBlockRunner<Nothing?> {

  override suspend fun initialize(interaction: IBlockInteraction): Nothing? = null

  override suspend fun run(cursor: IRichCursor): IBlockExit {
    return try {
      println(block.type + evaluateToString(block.config.message, context))
      setContactProperty(block, context)
      firstTrueOrNullBlockExitOrThrow(block, context)
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOrThrow(block)
    }
  }
}
