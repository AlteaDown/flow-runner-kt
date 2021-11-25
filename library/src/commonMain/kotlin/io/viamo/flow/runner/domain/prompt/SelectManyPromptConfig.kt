package io.viamo.flow.runner.domain.prompt

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.domain.prompt.SelectManyPrompt}.
 */
data class SelectManyPromptConfig(
  override val kind: String = SELECT_MANY_PROMPT_KEY,
  override val isResponseRequired: Boolean = true,
  override val prompt: String,
  override var value: List<String>?,
  override var isSubmitted: Boolean?,
  val choices: List<IChoice>,
) : IPromptConfig<List<String>?>
