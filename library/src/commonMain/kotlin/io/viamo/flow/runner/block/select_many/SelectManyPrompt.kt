package io.viamo.flow.runner.block.select_many

import PromptValidationException
import ValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.exceptions.InvalidChoiceException
import io.viamo.flow.runner.domain.prompt.BasePrompt

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
) : BasePrompt<List<String>?> {

  override val key = SELECT_MANY_PROMPT_KEY

  /* TODO: This will return true, or throw an error, but it seems like it should return a false instead of throwing error.
      Consider making a validateOrThrow and a and a validate, where validate only returns true/false. */
  override fun validate(value: Any?): Boolean {
    return (value as List<String>?)?.let {
      when {
        !config.isResponseRequired -> true
        value == null -> false
        value.isEmpty() -> throw ValidationException(INVALID_AT_LEAST_ONE_SELECTION_REQUIRED)
        else -> validateSelections(value)
      }
    } ?: throw IllegalStateException("Expected a List<String>?")
  }

  private fun validateSelections(selections: List<String>): Boolean {
    val invalidChoices = selections.filter { selection -> config.choices.none { it.key == selection } }
    if (invalidChoices.isNotEmpty()) {
      throw InvalidChoiceException(INVALID_ALL_SELECTIONS_MUST_EXIST_ON_BLOCK + invalidChoices)
    }
    return true
  }
}
