package io.viamo.flow.runner.flowspec.block.type.select_many

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.block.type.select_one.Choice
import kotlinx.serialization.Serializable

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.flowspec.block.SelectManyPrompt}.
 */
@Serializable
data class SelectManyPromptConfig(
  override val kind: String = SELECT_MANY_PROMPT_KEY,
  override val isResponseRequired: Boolean = true,
  override val prompt: String,
  override var value: List<String>?,
  override var isSubmitted: Boolean?,
  val choices: List<Choice>,
) : IPromptConfig<List<String>?>
