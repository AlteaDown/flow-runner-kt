package io.viamo.flow.runner.domain.runners

import io.viamo.flow.runner.domain.prompt.MessagePromptConfig
import io.viamo.flow.runner.flowspec.*
import io.viamo.flow.runner.model.block.IMessageBlock
import io.viamo.flow.runner.model.block.IMessageBlockConfig

/**
 * Block runner for "MobilePrimitives\Message" - Presents a single message to the contact. The form of the message can
 * depend on the channel: e.g. a voice recording for the ivr channel, and text for the text channel.
 *
 * - text (SMS): Sends message as an SMS to the contact.
 * - text (USSD): Displays message as the next USSD prompt to the user. (Note on USSD session management: If there are
 *   following blocks in the flow, the user has an opportunity to reply with anything to proceed. If there are no
 *   following blocks, the contact is prompted to dismiss the session.)
 * - ivr: Plays message-audio to the contact.
 * - rich_messaging: Display message within the conversation with the contact. Optionally, platforms may attach the
 *   message-prompt (if provided) as an audio attachment that the contact can choose to play.
 * - offline: Display message within the session with the contact.
 */
class MessageBlockRunner(
  override val block: IMessageBlock,
  override val context: IContext,
) : IBlockRunner<Nothing?, IMessageBlockConfig, MessagePromptConfig> {

  override suspend fun initialize(interaction: IBlockInteraction): MessagePromptConfig {
    return MessagePromptConfig(this.block.config.prompt, interaction.has_response)
  }

  override suspend fun run(cursor: IRichCursor<Nothing?, IMessageBlockConfig, MessagePromptConfig>): IBlockExit {
    return firstTrueOrNullBlockExitOrThrow(this.block, this.context)
  }
}
