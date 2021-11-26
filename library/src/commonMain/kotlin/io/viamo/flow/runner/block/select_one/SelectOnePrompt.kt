import io.viamo.flow.runner.block.select_many.SELECT_MANY_PROMPT_KEY
import io.viamo.flow.runner.block.select_one.SelectOnePromptConfig
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt

const val SELECT_ONE_PROMPT_KEY = "SelectOne"

/**
 * Concrete implementation of {@link io.viamo.flow.runner.domain.prompt.io.viamo.flow.runner.jsMain.kotlin."flow-runner".BasePrompt} to request, at most, one selection from multiple choices, from an
 * {@link io.viamo.flow.runner."flow-spec".IContact}.
 */
data class SelectOnePrompt(
  override val config: SelectOnePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<String?> {

  override val key = SELECT_MANY_PROMPT_KEY

  override fun validate(value: Any?): Boolean {
    (value as String?).let { stringVal ->
      if (config.isResponseRequired && config.choices.none { it.key == stringVal }) {
        throw ValidationException("Value provided must be in list of choices")
      } else {
        return true
      }
    }
  }
}
