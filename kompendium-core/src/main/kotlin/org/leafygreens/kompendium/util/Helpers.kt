package org.leafygreens.kompendium.util

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.util.InternalAPI
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaField
import org.slf4j.LoggerFactory

object Helpers {

  private val logger = LoggerFactory.getLogger(javaClass)

  const val COMPONENT_SLUG = "#/components/schemas"

  /**
   * TODO Explain this
   */
  @OptIn(InternalAPI::class)
  fun Route.calculatePath(tail: String = ""): String {
    logger.info("Building path for ${selector::class}")
    return when (selector) {
      is RootRouteSelector -> {
        logger.info("Root route detected, returning path: $tail")
        tail
      }
      is PathSegmentParameterRouteSelector -> {
        logger.info("Found segment parameter $selector, continuing to parent")
        val newTail = "/$selector$tail"
        parent?.calculatePath(newTail) ?: run {
          logger.info("No parent found, returning current path")
          newTail
        }
      }
      is PathSegmentConstantRouteSelector -> {
        logger.info("Found segment constant $selector, continuing to parent")
        val newTail = "/$selector$tail"
        parent?.calculatePath(newTail) ?: run {
          logger.info("No parent found, returning current path")
          newTail
        }
      }
      else -> when (selector.javaClass.simpleName) {
        // dumb ass workaround to this object being internal to ktor
        "TrailingSlashRouteSelector" -> {
          logger.info("Found trailing slash route selector")
          val newTail = "$tail/"
          parent?.calculatePath(newTail) ?: run {
            logger.info("No parent found, returning current path")
            newTail
          }
        }
        else -> error("Unhandled selector type ${selector::class}")
      }
    }
  }

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
    entities.forEach { (name, entity) -> logger.info("Ahead of $functionName invocation, $name: $entity") }
    val result = block.invoke()
    logger.info("Result of $functionName invocation: $result")
    return result
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
