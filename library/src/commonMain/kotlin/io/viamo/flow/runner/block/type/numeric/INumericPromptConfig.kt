package io.viamo.flow.runner.block.type.numeric

import io.viamo.flow.runner.domain.prompt.IPromptConfig

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.block.NumericPrompt}.
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
