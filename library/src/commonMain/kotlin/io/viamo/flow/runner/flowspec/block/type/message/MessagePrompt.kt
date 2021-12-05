package io.viamo.flow.runner.flowspec.block.type.message

import PromptValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import kotlinx.serialization.Serializable

const val MESSAGE_PROMPT_KEY = "Message"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to present a message to an {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
@Serializable
data class MessagePrompt(
  override val config: MessagePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<Nothing?> {

  override val key = MESSAGE_PROMPT_KEY

  override fun validate(value: Any?) = true
}