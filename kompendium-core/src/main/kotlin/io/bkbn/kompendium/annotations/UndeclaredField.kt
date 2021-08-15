package io.bkbn.kompendium.annotations

import kotlin.reflect.KClass

//@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class UndeclaredField(val field: String, val clazz: KClass<*>)
