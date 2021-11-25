package io.viamo.flow.runner.domain.prompt

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.domain.prompt.NumericPrompt}.
 */
data class NumericPromptConfig(
  override val prompt: String,
  override var value: Double?,
  override var isSubmitted: Boolean?,
  val min: Double?,
  val max: Double?,
) : IPromptConfig<Double> {
  override val isResponseRequired: Boolean = false
  override val kind: String = NUMERIC_PROMPT_KEY
}
