package io.bkbn.kompendium.core.util

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.javaField
import org.slf4j.LoggerFactory
import java.util.Locale

object Helpers {

  private val logger = LoggerFactory.getLogger(javaClass)

  private const val COMPONENT_SLUG = "#/components/schemas"

  val UNIT_TYPE by lazy { Unit::class.createType() }

  /**
   * Higher order function that takes a map of names to object and will log their state ahead of function invocation
   * along with the result of the function invocation
   */
  fun <T> logged(functionName: String, entities: Map<String, Any>, block: () -> T): T {
    entities.forEach { (name, entity) -> logger.debug("Ahead of $functionName invocation, $name: $entity") }
    val result = block.invoke()
    logger.debug("Result of $functionName invocation: $result")
    return result
  }

  fun KClass<*>.getSimpleSlug(prop: KProperty<*>): String = when {
    this.typeParameters.isNotEmpty() -> genericNameAdapter(this, prop)
    else -> simpleName ?: error("Could not determine simple name for $this")
  }

  fun KType.getSimpleSlug(): String = when {
    this.arguments.isNotEmpty() -> genericNameAdapter(this, classifier as KClass<*>)
    else -> (classifier as KClass<*>).simpleName ?: error("Could not determine simple name for $this")
  }

  fun KType.getReferenceSlug(): String = when {
    arguments.isNotEmpty() -> "$COMPONENT_SLUG/${genericNameAdapter(this, classifier as KClass<*>)}"
    else -> "$COMPONENT_SLUG/${(classifier as KClass<*>).simpleName}"
  }

  /**
   * Will build a reference slug that is useful for schema caching and references, particularly
   * in the case of a class with type parameters
   */
  fun KClass<*>.getReferenceSlug(prop: KProperty<*>): String = when {
    this.typeParameters.isNotEmpty() -> "$COMPONENT_SLUG/${genericNameAdapter(this, prop)}"
    else -> "$COMPONENT_SLUG/${simpleName}"
  }

  /**
   * Adapts a class with type parameters into a reference friendly string
   */
  fun genericNameAdapter(field: KClass<*>, prop: KProperty<*>): String {
    val typeArgs = (prop.javaField?.genericType as ParameterizedType).actualTypeArguments
    val classNames = typeArgs.map { it as Class<*> }.map { it.kotlin }.map { it.simpleName }
    return classNames.joinToString(separator = "-", prefix = "${field.simpleName}-")
  }

  /**
   * Adapts a class with type parameters into a reference friendly string
   */
  fun genericNameAdapter(type: KType, clazz: KClass<*>): String {
    val classNames = type.arguments
      .map { it.type?.classifier as KClass<*> }
      .map { it.simpleName }
    return classNames.joinToString(separator = "-", prefix = "${clazz.simpleName}-")
  }

  fun String.capitalized() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
  }

  fun String.toNumber(): Number {
    return try {
      this.toInt()
    } catch (e: NumberFormatException) {
      this.toDouble()
    }
  }
}
