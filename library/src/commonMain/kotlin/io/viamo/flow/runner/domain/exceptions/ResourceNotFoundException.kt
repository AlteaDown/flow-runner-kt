/**
 * Specialized exception for {@link io.viamo.flow.runner."flow-spec".IResource} lookups.
 */
class ResourceNotFoundException(message: String? = null, cause: Throwable? = null) : Error(message, cause)
