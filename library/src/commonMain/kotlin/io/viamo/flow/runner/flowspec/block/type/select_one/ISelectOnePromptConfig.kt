package io.viamo.flow.runner.flowspec.block.type.select_one

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import kotlinx.serialization.Serializable

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.flowspec.block.type.select_one.SelectOnePrompt}.
 */
@Serializable
data class SelectOnePromptConfig(
  override val prompt: String,
  override var value: String?,
  override var isSubmitted: Boolean?,
  val choices: List<Choice>,
  val emptyChoicesMessage: String?,
  override val kind: String = SELECT_ONE_PROMPT_KEY,
  override val isResponseRequired: Boolean = true,
) : IPromptConfig<String?>

interface IChoice {
  val key: String
  val value: String
}

@Serializable
data class Choice(
  override val key: String,
  override val value: String,
) : IChoice
