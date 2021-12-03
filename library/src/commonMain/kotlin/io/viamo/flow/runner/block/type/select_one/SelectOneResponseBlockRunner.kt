package io.viamo.flow.runner.block.type.select_one

import io.viamo.flow.runner.block.IBlockExit
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.IRichCursor

/**
 * Block runner for "MobilePrimitives\SelectOneResponse" - Obtains the answer to a Multiple Choice question from the
 * contact. The contact must choose a single choice from a set of choices.
 *
 * - text (SMS): Send an SMS with the prompt text, according to the prompt configuration in config above, and wait to
 *   capture a response. (Lack a response after the flow's configured timeout, or an invalid response: proceed through
 *   the error exit.)
 * - text (USSD): Display a USSD menu prompt with the prompt text, according to the prompt configuration in config
 *   above, then wait to capture the menu response. (Dismissal of the session, timeout, or invalid response: proceed
 *   through the error exit.)
 * - ivr: Play the audio prompt, according to the prompt configuration in config above, then wait to capture the DTMF
 *   response. (Hangup, timeout, or invalid response: proceed through the error exit.)
 * - rich_messaging: Display the prompt text according to the prompt configuration in config above. Platforms may wait
 *   to capture a text response, or display rich menu items for each choice and wait to capture a menu choice.
 *   (If displaying menu items, platforms should display only question_prompt.) (Timeout or invalid response: proceed
 *   through the error exit.)
 * - offline: Display the prompt text according to question_prompt, and a menu of items for all choices. Wait to capture
 *   a menu selection.
 */
class SelectOneResponseBlockRunner(
  override val block: ISelectOneResponseBlock,
  override val context: Context,
) : IBlockRunner<String?> {

  override suspend fun initialize(interaction: IBlockInteraction): SelectOnePromptConfig {
    return SelectOnePromptConfig(
      prompt = block.config.prompt,
      value = interaction.value,
      choices = block.config.choices.entries.map { Choice(it.key, it.value) },
      isSubmitted = interaction.has_response,
      emptyChoicesMessage = "TODO: This has always been unset"
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
