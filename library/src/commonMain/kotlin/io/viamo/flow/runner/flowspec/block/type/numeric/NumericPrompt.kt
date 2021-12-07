package io.viamo.flow.runner.flowspec.block.type.numeric

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.flowspec.Context
import kotlinx.serialization.Serializable

const val NUMERIC_PROMPT_KEY = "Numeric"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request a number, optionally within particular bounds, from an
 * {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
@Serializable
data class NumericPrompt(
  override val context: Context,
  override val config: NumericPromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<Double>() {

  override val key = NUMERIC_PROMPT_KEY

  override fun validate(value: Any?): Boolean {
    return if (value is Double?) {
      when {
        value == null -> false
        value.isNaN() -> false
        config.min != null && value < config.min -> throw ValidationException("Value provided is less than allowed")
        config.max != null && value > config.max -> throw ValidationException("Value provided is greater than allowed")
        else -> true
      }
    } else {
      false
    }
  }
}
