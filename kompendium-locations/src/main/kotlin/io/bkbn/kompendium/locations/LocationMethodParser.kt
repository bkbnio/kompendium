package io.bkbn.kompendium.locations

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.metadata.method.MethodInfo
import io.bkbn.kompendium.core.parser.IMethodParser
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.ktor.application.feature
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@OptIn(KtorExperimentalLocationsAPI::class)
object LocationMethodParser : IMethodParser {
//  override fun KType.toParameterSpec(info: MethodInfo<*, *>, feature: Kompendium): List<Parameter> {
//    TODO()
//  }

  @Suppress("UnusedPrivateMember")
  private inline fun <reified T> yoinkParent() {
    val clazz = T::class as KClass<*>
    val parent = clazz.memberProperties
      .find { (it.returnType.classifier as KAnnotatedElement).hasAnnotation<Location>() }
    println(parent?.returnType)
  }

  fun KClass<*>.calculateLocationPath(suffix: String = ""): String {
    val locationAnnotation = this.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val parent = this.java.declaringClass?.kotlin
    val newSuffix = locationAnnotation.path.plus(suffix)
    return when (parent) {
      null -> newSuffix
      else -> parent.calculateLocationPath(newSuffix)
    }
  }

  inline fun <reified TParam : Any> processBaseInfo(
    paramType: KType,
    requestType: KType,
    responseType: KType,
    info: MethodInfo<*, *>,
    route: Route
  ): LocationBaseInfo {
    val locationAnnotation = TParam::class.findAnnotation<Location>()
    require(locationAnnotation != null) { "Location annotation must be present to leverage notarized location api" }
    val path = route.calculateRoutePath()
    val locationPath = TParam::class.calculateLocationPath()
    val pathWithLocation = path.plus(locationPath)
    val feature = route.application.feature(Kompendium)
    feature.config.spec.paths.getOrPut(pathWithLocation) { Path() }
    val baseInfo = parseMethodInfo(info, paramType, requestType, responseType, feature)
    return LocationBaseInfo(baseInfo, feature, pathWithLocation)
  }

  data class LocationBaseInfo(
    val op: PathOperation,
    val feature: Kompendium,
    val path: String
  )
}
