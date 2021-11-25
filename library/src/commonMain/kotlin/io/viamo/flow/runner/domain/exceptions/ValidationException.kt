/**
 * Generic exception for performing domain logic validation.
 */
class ValidationException(message: String, cause: Throwable? = null) : Error(message, cause)
