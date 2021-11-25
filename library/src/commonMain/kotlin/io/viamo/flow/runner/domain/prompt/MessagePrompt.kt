package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import io.viamo.flow.runner.domain.IFlowRunner

const val MESSAGE_PROMPT_KEY = "Message"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to present a message to an {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
data class MessagePrompt(
  override val config: MessagePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<Nothing?, MessagePromptConfig>(config, interactionId, runner, error) {

  override val key = MESSAGE_PROMPT_KEY

  override fun validate(value: Nothing?): Boolean {
    return true
  }
}
