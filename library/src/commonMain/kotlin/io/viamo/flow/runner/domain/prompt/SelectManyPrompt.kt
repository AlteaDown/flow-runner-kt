package io.viamo.flow.runner.domain.prompt

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.exceptions.InvalidChoiceException

const val INVALID_AT_LEAST_ONE_SELECTION_REQUIRED = "At least one selection is required, but none provided"
const val INVALID_ALL_SELECTIONS_MUST_EXIST_ON_BLOCK = "All selections must be valid choices on block"
const val SELECT_MANY_PROMPT_KEY = "SelectMany"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request a selection from multiple choices, optionally requiring at
 * least one, from an {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
data class SelectManyPrompt(
  override val config: SelectManyPromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<List<String>?, SelectManyPromptConfig>(config, interactionId, runner, error) {

  override val key = SELECT_MANY_PROMPT_KEY

  /* TODO: This will return true, or throw an error, but it seems like it should return a false instead of throwing error.
      Consider making a validateOrThrow and a and a validate, where validate only returns true/false. */
  override fun validate(selections: List<String>?): Boolean {
    val isResponseRequired = this.config.isResponseRequired
    val choices = this.config.choices

    return if (!isResponseRequired) {
      true
    } else if (selections == null) {
      false
    } else if (selections.isEmpty()) {
      throw ValidationException(INVALID_AT_LEAST_ONE_SELECTION_REQUIRED)
    } else {
      val invalidChoices = selections.filter { selection -> choices.none { it.key == selection } }
      if (invalidChoices.isNotEmpty()) {
        throw InvalidChoiceException(INVALID_ALL_SELECTIONS_MUST_EXIST_ON_BLOCK + invalidChoices)
      }

      true
    }
  }
}
