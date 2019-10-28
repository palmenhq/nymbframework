package org.nymbframework.core.environment

/**
 * Indicates that a component was expected in the [Environment] but was not present.
 */
class ComponentNotFoundException(message: String) : RuntimeException(message)
