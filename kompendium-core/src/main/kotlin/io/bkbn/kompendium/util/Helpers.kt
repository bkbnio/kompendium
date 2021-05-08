package io.bkbn.kompendium.util

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.javaField
import org.slf4j.LoggerFactory

object Helpers {

  private val logger = LoggerFactory.getLogger(javaClass)

  const val COMPONENT_SLUG = "#/components/schemas"

  val UNIT_TYPE by lazy { Unit::class.createType() }

  /**
   * Simple extension function that will take a [Pair] and place it (if absent) into a [MutableMap].
   *
   * @receiver [MutableMap]
   * @param pair to add to map
   */
  fun <K, V> MutableMap<K, V>.putPairIfAbsent(pair: Pair<K, V>) = putIfAbsent(pair.first, pair.second)

  /**
   * Simple extension function that will convert a list with two items into a [Pair]
   * @receiver [List]
   * @return [Pair]
   * @throws [IllegalArgumentException] when the list size is not exactly two
   */
  fun <T> List<T>.toPair(): Pair<T, T> {
    if (this.size != 2) {
      throw IllegalArgumentException("List is not of length 2!")
    }
    return Pair(this[0], this[1])
  }

  /**
   * Higher order function that takes a map of names to objects and will log their state ahead of function invocation
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
}
