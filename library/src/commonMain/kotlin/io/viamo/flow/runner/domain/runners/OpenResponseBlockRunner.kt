import io.viamo.flow.runner.domain.prompt.OpenPromptConfig
import io.viamo.flow.runner.domain.runners.IBlockRunner
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IOpenResponseBlock
import io.viamo.flow.runner.model.block.IOpenResponseBlockConfig

/**
 * Block runner for "MobilePrimitives.OpenResponse" - Obtains an open-ended response from the contact. Dependent on the
 * channel, this is a text response, audio recording, or other type of media recording (e.g. video).
 *
 * - text (SMS): Send an SMS with the prompt text, according to the prompt configuration in config above, and wait to
 *   capture a response. (Lack a response after the flow's configured timeout: proceed through the error exit.)
 * - text (USSD): Display a USSD menu prompt with the prompt text, according to the prompt configuration in config
 *   above, then wait to capture the menu response. (Dismissal of the session or timeout: proceed through the error
 *   exit.)
 * - ivr: Play the audio prompt, acccording to the prompt configuration in config above, then wait to capture the DTMF
 *   reponse. (Hangup or timeout with nothing recorded: proceed through the error exit.)
 * - rich_messaging: Display the prompt text according to the prompt configuration in config above, and wait to capture
 *   a text response or an upload (audio, video) from the contact. (Timeout: proceed through the error exit.)
 * - offline: Display the prompt text according to the prompt configuration in config above, and display a text entry
 *   widget. Wait to capture a response.
 */
class OpenResponseBlockRunner(
  override val block: IOpenResponseBlock,
  override val context: IContext,
) : IBlockRunner<String?, IOpenResponseBlockConfig, OpenPromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): OpenPromptConfig {
    return OpenPromptConfig(
      prompt = block.config.prompt,
      maxResponseCharacters = block.config.text?.max_response_characters,
      value = interaction.value,
      isSubmitted = interaction.has_response
    )
  }

  override suspend fun run(cursor: IRichCursor<String?, IOpenResponseBlockConfig, OpenPromptConfig>): IBlockExit {
    return try {
      setContactProperty(this.block, this.context)
      firstTrueOrNullBlockExitOrThrow(this.block, this.context)
    } catch (e: Throwable) {
      e.printStackTrace()
      findDefaultBlockExitOrThrow(this.block)
    }
  }
}
