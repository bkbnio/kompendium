package io.bkbn.kompendium.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class KompendiumField(val name: String)
