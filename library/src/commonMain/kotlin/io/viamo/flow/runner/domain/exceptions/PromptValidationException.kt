import io.viamo.flow.runner.flowspec.ThrowableSerializer
import kotlinx.serialization.Serializable

/**
 * Specialized validation exception to be predictably raised by {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
 */
@Serializable
data class PromptValidationException(
  override val message: String? = null,

  @Serializable(with = ThrowableSerializer::class)
  override val cause: Throwable? = null
) : Error(message, cause)

