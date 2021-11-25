package io.viamo.flow.runner.domain.prompt

import SELECT_ONE_PROMPT_KEY

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link SelectOnePrompt}.
 */
data class SelectOnePromptConfig(
  override val prompt: String,
  override var value: String?,
  override var isSubmitted: Boolean?,
  val choices: List<IChoice>,
  val emptyChoicesMessage: String?,
  override val kind: String = SELECT_ONE_PROMPT_KEY,
  override val isResponseRequired: Boolean = true,
) : IPromptConfig<String?>

interface IChoice {
  val key: String
  val value: String
}

data class Choice(
  override val key: String,
  override val value: String,
) : IChoice
