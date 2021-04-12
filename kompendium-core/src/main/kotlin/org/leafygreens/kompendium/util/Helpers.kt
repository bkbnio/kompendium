package org.leafygreens.kompendium.util

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.util.InternalAPI
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.models.FormatSchema
import org.leafygreens.kompendium.models.ObjectSchema
import org.leafygreens.kompendium.models.SimpleSchema

object Helpers {

  @OptIn(InternalAPI::class)
  fun Route.calculatePath(tail: String = ""): String = when (selector) {
    is RootRouteSelector -> tail
    is PathSegmentParameterRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/{$selector}$tail"
    is PathSegmentConstantRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/$selector$tail"
    else -> error("unknown selector type $selector")
  }

  fun objectSchema(clazz: KClass<*>): ObjectSchema = ObjectSchema(
    properties =
    clazz.memberProperties.associate { prop ->
      val field = prop.javaField?.type?.kotlin
      val anny = prop.findAnnotation<KompendiumField>()
      // TODO How to handle arrays?
      val schema = when (field) {
        Int::class -> FormatSchema("int32", "integer")
        Long::class -> FormatSchema("int64", "integer")
        Double::class -> FormatSchema("double", "number")
        Float::class -> FormatSchema("float", "number")
        String::class -> SimpleSchema("string")
        Boolean::class -> SimpleSchema("boolean")
        else -> Helpers.objectSchema(field as KClass<*>)
      }
      Pair(prop.name, schema)
    })

}
