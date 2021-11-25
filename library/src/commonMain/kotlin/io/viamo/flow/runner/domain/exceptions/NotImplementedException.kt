package io.viamo.flow.runner.domain.exceptions

/**
 * Generic exception for stunted extension, or in-progress implementations.
 */
class NotImplementedException(message: String? = null, cause: Throwable? = null) : Error(message, cause)