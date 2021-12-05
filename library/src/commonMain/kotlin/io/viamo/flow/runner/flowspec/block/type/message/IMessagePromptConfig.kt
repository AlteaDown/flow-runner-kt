package io.viamo.flow.runner.flowspec.block.type.message

import io.viamo.flow.runner.domain.prompt.IPromptConfig
import io.viamo.flow.runner.flowspec.NothingNullableSerializer
import kotlinx.serialization.Serializable

/**
 * Interface for defining an {@link io.viamo.flow.runner.domain.prompt.IPromptConfig} resolving to a {@link io.viamo.flow.runner.flowspec.block.MessagePrompt}.
 */
@Serializable
data class MessagePromptConfig(
  override val prompt: String,
  override var isSubmitted: Boolean?,
) : IPromptConfig<Nothing?> {

  override val kind: String = "Message"
  override val isResponseRequired: Boolean = false

  @Serializable(with = NothingNullableSerializer::class)
  override var value: Nothing? = null
}
