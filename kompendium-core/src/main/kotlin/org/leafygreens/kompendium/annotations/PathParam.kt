package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class PathParam(val description: String = "")
