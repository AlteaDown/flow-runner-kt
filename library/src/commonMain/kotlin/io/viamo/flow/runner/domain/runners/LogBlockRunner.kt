package io.viamo.flow.runner.domain.runners

import io.viamo.flow.runner.domain.createFormattedDate
import io.viamo.flow.runner.domain.prompt.INoPromptConfig
import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.ext.toJsonElement
import io.viamo.flow.runner.ext.toJsonPrimitive
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.ILogBlock
import io.viamo.flow.runner.model.block.ILogBlockConfig

/**
 * Block runner for "Core.Log" - Appends a low-level message to {@link io.viamo.flow.runner."flow-spec".IContext.logs}.
 *
 * The io.viamo.flow.runner."flow-spec".Context for a Flow shall have a log key, which preserves a mapping of timestamps and log messages for debugging.
 * These logs must be maintained for the duration of the Run, and may be maintained for a longer period. The Log Block
 * provides one way for a Flow to write to this log. On executing this block, the platform will append a key/value pair
 * to the log object within the io.viamo.flow.runner."flow-spec".Context as below, and then proceed to the next block.
 */
class LogBlockRunner(
  override val block: ILogBlock,
  override var context: Context
) : IBlockRunner<Nothing?, ILogBlockConfig, INoPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): IPromptConfig? = null

  override suspend fun run(cursor: IRichCursor<Nothing?, ILogBlockConfig, INoPromptConfig>): IBlockExit {
    return try {
      context = context.copy(logs = (context.logs.toMap() + newLog()).toJsonElement())
      setContactProperty(block, context)
      firstTrueOrNullBlockExitOrThrow(block, context)
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOrThrow(block)
    }
  }

  private fun newLog() = createFormattedDate().toJsonPrimitive() to evaluateToString(block.config.message, context).toJsonPrimitive()
}
