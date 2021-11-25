import io.viamo.flow.runner.domain.prompt.INoPromptConfig
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IOutputBlock
import io.viamo.flow.runner.model.block.IOutputBlockConfig

/**
 * Block runner for "Core.Output" - This block provides a connection to the
 * [Flow Results specification](https://github.com/FLOIP/flow-results/blob/master/specification.md), by storing a named
 * Output variable.
 *
 * Not all block interactions and low-level logs are important to users; most users are concerned with a subset of
 * results that have specific meaning -- the "Flow Results". (See Flow Results specification.) Any block type, as part
 * of its specified runtime behaviour, may write to the Flow Results. The Output Block is a low-level block that does
 * just simply one thing: write a named variable corresponding to the name of the block to the Flow Results, determined
 * by the value expression.
 */
class OutputBlockRunner(
  override val block: IOutputBlock,
  override val context: IContext,
) : IBlockRunner<Nothing?, IOutputBlockConfig, INoPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): INoPromptConfig? = null
  override suspend fun run(cursor: IRichCursor<Nothing?, IOutputBlockConfig, INoPromptConfig>): IBlockExit {
    try {
      cursor.interaction.value = evaluateToString(this.block.config.value, createEvalContextFrom(this.context))
      cursor.interaction.has_response = true
      setContactProperty(this.block, this.context)
      return firstTrueOrNullBlockExitOrThrow(this.block, this.context)
    } catch (e: Throwable) {
      e.printStackTrace()
      return findDefaultBlockExitOrThrow(this.block)
    }
  }
}
