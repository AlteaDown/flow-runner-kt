package io.viamo.flow.runner.domain.exceptions

/**
 * Generic exception for selection validation; typically leveraged by {@link io.viamo.flow.runner.domain.prompt.IPrompt} implementations.
 */
class InvalidChoiceException(message: String? = null, cause: Throwable? = null) : Error(message, cause)
