package io.bkbn.kompendium.annotations

/**
 * Annotation used to perform field level overrides.
 * @param name Indicates that a field name override is desired.  Often used for camel case to snake case conversions.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Field(val name: String = "", val description: String = "")
