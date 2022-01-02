package io.bkbn.kompendium.annotations.constraint

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class MultipleOf(val multiple: Int)
