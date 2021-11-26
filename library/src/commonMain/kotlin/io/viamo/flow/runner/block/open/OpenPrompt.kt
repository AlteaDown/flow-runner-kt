package io.viamo.flow.runner.block.open

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt

const val OPEN_PROMPT_KEY = "Open"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request a String of text, optionally with a maximum length boundary,
 * from an {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
data class OpenPrompt(
  override val config: OpenPromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<String?> {
  override val key = OPEN_PROMPT_KEY

  override fun validate(value: Any?): Boolean = (value as String?).let {
    if (config.maxResponseCharacters != null && value != null && value.length > config.maxResponseCharacters) {
      throw ValidationException("Too many characters on value provided")
    } else {
      true
    }
  }
}
