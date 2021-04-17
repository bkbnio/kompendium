package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class CookieParam(val description: String = "")
