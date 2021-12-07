package io.viamo.flow.runner.flowspec.block.type.select_one

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.flowspec.Context
import io.viamo.flow.runner.flowspec.block.type.select_many.SELECT_MANY_PROMPT_KEY
import kotlinx.serialization.Serializable

const val SELECT_ONE_PROMPT_KEY = "SelectOne"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request, at most, one selection from multiple choices, from an
 * {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
@Serializable
data class SelectOnePrompt(
  override val context: Context,
  override val config: SelectOnePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<String?>() {

  override val key = SELECT_MANY_PROMPT_KEY

  override fun validate(value: Any?): Boolean {
    return when {
      value !is String? -> false
      config.isResponseRequired && config.choices.none { it.key == value } -> throw ValidationException("Value provided must be in list of choices")
      else -> true
    }
  }
}
