package io.viamo.flow.runner.block.numeric

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt

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
) : BasePrompt<Double> {

  override val key = NUMERIC_PROMPT_KEY

  override fun validate(value: Any?) = (value as Double?).let {
    when {
      it == null -> false
      it.isNaN() -> false
      config.min != null && it < config.min -> throw ValidationException("Value provided is less than allowed")
      config.max != null && it > config.max -> throw ValidationException("Value provided is greater than allowed")
      else -> true
    }
  }
}
