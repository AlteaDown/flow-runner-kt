/**
 * Specialized validation exception to be predictably raised by {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
 */
class PromptValidationException(message: String? = null, cause: Throwable? = null) : Error(message, cause)
