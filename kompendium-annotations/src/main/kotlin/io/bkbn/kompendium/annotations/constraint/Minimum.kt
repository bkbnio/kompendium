package io.bkbn.kompendium.annotations.constraint

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Minimum(val min: String, val exclusive: Boolean = false)
