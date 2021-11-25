package io.viamo.flow.runner.domain.runners

import io.viamo.flow.runner.domain.prompt.NumericPromptConfig
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.INumericBlockConfig
import io.viamo.flow.runner.model.block.INumericResponseBlock

/**
 * Block runner for "MobilePrimitives\NumericResponse" - Obtains a numeric response from the contact.
 *
 * - text (SMS): Send an SMS with the prompt text, according to the prompt configuration in config above, and wait to
 *   capture a response. (Lack a response after the flow's configured timeout, or an invalid response: proceed through
 *   the error exit.)
 * - text (USSD): Display a USSD menu prompt with the prompt text, according to the prompt configuration in config
 *   above, then wait to capture the menu response. (Dismissal of the session, timeout, or invalid response: proceed
 *   through the error exit.)
 * - ivr: Play the audio prompt, acccording to the prompt configuration in config above, then wait to capture the DTMF
 *   reponse. (Hangup, timeout, or invalid response: proceed through the error exit.)
 * - rich_messaging: Display the prompt text according to the prompt configuration in config above. Platforms may wait
 *   to capture a text response, or display a numeric entry widget and wait to capture a response. (Timeout or invalid
 *   response: proceed through the error exit.)
 * - offline: Display the prompt text according to the prompt configuration in config above, and display a numeric entry
 *   widget. Wait to capture a response.
 */
class NumericResponseBlockRunner(
  override val block: INumericResponseBlock,
  override val context: IContext,
) : IBlockRunner<Double, INumericBlockConfig, NumericPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): NumericPromptConfig {
    return NumericPromptConfig(
      prompt = block.config.prompt,
      min = block.config.validation_minimum,
      max = block.config.validation_maximum,
      value = interaction.value,
      isSubmitted = interaction.has_response
    )
  }

  override suspend fun run(cursor: IRichCursor<Double, INumericBlockConfig, NumericPromptConfig>): IBlockExit {
    return try {
      setContactProperty(block, context)
      firstTrueOrNullBlockExitOrThrow(block, context)
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOrThrow(block)
    }
  }
}
