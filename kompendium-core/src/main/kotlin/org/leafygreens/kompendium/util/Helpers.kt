package org.leafygreens.kompendium.util

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.util.InternalAPI
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.Kontent
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.models.oas.ArraySchema
import org.leafygreens.kompendium.models.oas.DictionarySchema
import org.leafygreens.kompendium.models.oas.EnumSchema
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponentSchema
import org.leafygreens.kompendium.models.oas.SimpleSchema
import org.slf4j.LoggerFactory

object Helpers {

  private val logger = LoggerFactory.getLogger(javaClass)

  const val COMPONENT_SLUG = "#/components/schemas"

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

  fun <K, V> MutableMap<K, V>.putPairIfAbsent(pair: Pair<K, V>) = putIfAbsent(pair.first, pair.second)

  fun <T> List<T>.toPair(): Pair<T, T> {
    if (this.size != 2) {
      throw IllegalArgumentException("List is not of length 2!")
    }
    return Pair(this[0], this[1])
  }

  fun genericNameAdapter(field: KClass<*>, prop: KProperty<*>): String {
    val typeArgs = (prop.javaField?.genericType as ParameterizedType).actualTypeArguments
    val classNames = typeArgs.map { it as Class<*> }.map { it.kotlin }.map { it.simpleName }
    return classNames.joinToString(separator = "-", prefix = "${field.simpleName}-")
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
}
