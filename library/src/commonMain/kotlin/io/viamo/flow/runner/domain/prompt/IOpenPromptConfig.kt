package io.viamo.flow.runner.domain.prompt

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.domain.prompt.OpenPrompt}.
 */
data class OpenPromptConfig(
  override val prompt: String,
  override var value: String?,
  override var isSubmitted: Boolean?,
  val maxResponseCharacters: Int?,
) : IPromptConfig<String?> {

  override val kind: String = OPEN_PROMPT_KEY
  override val isResponseRequired: Boolean = true
}
