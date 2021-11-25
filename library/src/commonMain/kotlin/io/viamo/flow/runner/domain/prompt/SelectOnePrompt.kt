import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt
import io.viamo.flow.runner.domain.prompt.SELECT_MANY_PROMPT_KEY
import io.viamo.flow.runner.domain.prompt.SelectOnePromptConfig

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
) : BasePrompt<String?, SelectOnePromptConfig>(config, interactionId, runner, error) {

  override val key = SELECT_MANY_PROMPT_KEY

  override fun validate(value: String?): Boolean {
    val isResponseRequired = this.config.isResponseRequired
    val choices = this.config.choices

    if (isResponseRequired && choices.none { it.key == value }) {
      throw ValidationException("Value provided must be in list of choices")
    }

    return true
  }
}
