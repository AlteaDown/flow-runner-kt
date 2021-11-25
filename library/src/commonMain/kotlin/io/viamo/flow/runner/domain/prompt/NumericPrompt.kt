package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner

const val NUMERIC_PROMPT_KEY = "Numeric"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request a number, optionally within particular bounds, from an
 * {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
data class NumericPrompt(
  override val config: NumericPromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<Double, NumericPromptConfig>(config, interactionId, runner, error) {

  override val key = NUMERIC_PROMPT_KEY

  override fun validate(value: Double?): Boolean {
    if (value?.isNaN() != false) {
      return false
    }

    val min = this.config.min
    val max = this.config.max

    if (min != null && value < min) {
      throw ValidationException("Value provided is less than allowed")
    } else if (max != null && value > max) {
      throw ValidationException("Value provided is greater than allowed")
    }

    return true
  }
}
