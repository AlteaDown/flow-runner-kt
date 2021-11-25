package io.viamo.flow.runner.block.message

import io.viamo.flow.runner.domain.prompt.IPromptConfig

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.block.MessagePrompt}.
 */
data class MessagePromptConfig(
  override val prompt: String,
  override var isSubmitted: Boolean?,
) : IPromptConfig<Nothing?> {

  override val kind: String = "Message"
  override val isResponseRequired: Boolean = false
  override var value: Nothing? = null
}