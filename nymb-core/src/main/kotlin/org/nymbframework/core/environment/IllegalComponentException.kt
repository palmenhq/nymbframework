package org.nymbframework.core.environment

/**
 * Thrown if trying to register a component of a different type than the instance type.
 */
class IllegalComponentException(message: String) : RuntimeException(message)
