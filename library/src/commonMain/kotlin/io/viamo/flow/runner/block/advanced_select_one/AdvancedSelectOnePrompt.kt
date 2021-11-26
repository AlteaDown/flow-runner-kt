package io.viamo.flow.runner.block.advanced_select_one

import PromptValidationException
import io.viamo.flow.runner.domain.IFlowRunner
import io.viamo.flow.runner.domain.prompt.BasePrompt

const val ADVANCED_SELECT_ONE_PROMPT_KEY = "AdvancedSelectOne"

data class AdvancedSelectOnePrompt(
  override val config: AdvancedSelectOnePromptConfig,
  override val interactionId: String,
  override val runner: IFlowRunner,
  override var error: PromptValidationException? = null,
) : BasePrompt<List<AdvancedSelectOne>> {

  override val key = ADVANCED_SELECT_ONE_PROMPT_KEY

  /**
   * There is no validation as it's literally impossible for this to pass, as a CSV is needed in order to handle this, and the CSV
   * data never gets put into io.viamo.flow.runner.domain.FlowRunner.
   **/
  override fun validate(value: Any?): Boolean {
    return true
  }
}
