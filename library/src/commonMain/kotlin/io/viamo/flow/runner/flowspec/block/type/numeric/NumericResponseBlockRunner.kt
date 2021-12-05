package io.viamo.flow.runner.flowspec.block.type.numeric

import io.viamo.flow.runner.domain.IRichCursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit

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
  override val context: Context,
) : IBlockRunner<Double> {

  override suspend fun initialize(interaction: IBlockInteraction): NumericPromptConfig {
    return NumericPromptConfig(
      prompt = block.config.prompt,
      min = block.config.validation_minimum,
      max = block.config.validation_maximum,
      value = interaction.value?.toDouble(),
      isSubmitted = interaction.has_response
    )
  }

  override suspend fun run(cursor: IRichCursor): IBlockExit {
    return try {
      block.setContactProperty(context)
      block.firstTrueOrNullBlockExitOrThrow()
    } catch (e: Throwable) {
      e.printStackTrace()
      block.findDefaultBlockExitOrThrow()
    }
  }
}
