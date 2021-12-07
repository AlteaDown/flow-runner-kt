package io.viamo.flow.runner.flowspec.block.type.open

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import kotlinx.serialization.Serializable

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.flowspec.block.OpenPrompt}.
 */
@Serializable
data class OpenPromptConfig(
  override val prompt: String,
  override var value: String?,
  override var isSubmitted: Boolean?,
  val maxResponseCharacters: Int?,
) : IPromptConfig<String?> {

  override val kind: String = OPEN_PROMPT_KEY
  override val isResponseRequired: Boolean = true
}
