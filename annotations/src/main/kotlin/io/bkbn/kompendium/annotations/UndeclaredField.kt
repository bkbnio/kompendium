package io.bkbn.kompendium.annotations

import kotlin.reflect.KClass

/**
 * This annotation allows users to add additional fields that are not part of the core data model.  This should be used
 * EXTREMELY sparingly.  Most useful in supporting a variety of polymorphic serialization techniques.
 * @param field Name of the extra field to add to the model
 * @param clazz Class type of the field being added.  If this is a complex type, you are most likely doing something
 * wrong.
 */
@Repeatable
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class UndeclaredField(val field: String, val clazz: KClass<*>)
