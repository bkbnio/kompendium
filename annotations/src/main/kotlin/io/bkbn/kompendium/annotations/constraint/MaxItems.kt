package io.bkbn.kompendium.annotations.constraint

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxItems(val items: Int)
