import io.viamo.flow.runner.domain.prompt.INoPromptConfig
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IPrintBlock
import io.viamo.flow.runner.model.block.IPrintBlockConfig

/**
 * Block runner for "ConsoleIO\Print" - Prints a message to standard output, by evaluating an expression.
 */
class PrintBlockRunner(
  override val block: IPrintBlock,
  override val context: IContext
) : IBlockRunner<Nothing?, IPrintBlockConfig, INoPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): INoPromptConfig? = null

  override suspend fun run(cursor: IRichCursor<Nothing?, IPrintBlockConfig, INoPromptConfig>): IBlockExit {
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
