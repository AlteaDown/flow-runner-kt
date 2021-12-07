package io.viamo.flow.runner.flowspec.block.type.select_many

import io.viamo.flow.runner.domain.Cursor
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.IBlockInteraction
import io.viamo.flow.runner.flowspec.block.IBlockExit
import io.viamo.flow.runner.flowspec.block.type.select_one.Choice
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Block runner for "MobilePrimitives\SelectManyResponse" - Obtains the answer to a Multiple Choice question from the
 * contact. The contact can choose many choices from a set of choices.
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
class SelectManyResponseBlockRunner(
  override val block: ISelectManyResponseBlock,
  override val context: Context,
) : IBlockRunner<List<String>?> {

  override suspend fun initialize(interaction: IBlockInteraction): SelectManyPromptConfig {
    return SelectManyPromptConfig(
      prompt = block.config.prompt,
      value = interaction.value?.let { Json.decodeFromString(it) },
      choices = block.config.choices.entries.map { Choice(it.key, it.value) },
      isSubmitted = interaction.has_response,
    )
  }

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
