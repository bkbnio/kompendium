package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.createRouteFromPath
import io.ktor.routing.method
import io.ktor.util.pipeline.PipelineInterceptor
import org.leafygreens.kompendium.models.OpenApiSpec
import org.leafygreens.kompendium.models.OpenApiSpecInfo
import org.leafygreens.kompendium.models.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.util.Helpers.calculatePath
import org.leafygreens.kompendium.util.Helpers.objectSchema

data class RouteInfo(val summary: String, val description: String? = null)
data class MethodInfo(val summary: String, val description: String? = null, val tags: Set<String> = emptySet())

object Kompendium {
  val openApiSpec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  fun Route.notarizedRoute(path: String, info: RouteInfo, build: Route.() -> Unit): Route {
    val fullPath = calculatePath().plus(path)
    openApiSpec.paths.getOrPut(fullPath) { OpenApiSpecPathItem(summary = info.summary, description = info.description) }
    return createRouteFromPath(path).apply(build)
  }

  fun Route.notarizedGet(info: MethodInfo, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.get = OpenApiSpecPathItemOperation(
      summary = info.summary,
      description = info.description,
      tags = info.tags
    )
    return method(HttpMethod.Get) { handle(body) }
  }

  fun Route.notarizedPost(info: MethodInfo, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.post = OpenApiSpecPathItemOperation(
      summary = info.summary,
      description = info.description,
      tags = info.tags
    )
    return method(HttpMethod.Post) { handle(body) }
  }

  inline fun <reified TQ: Any, reified TP: Any, reified TR: Any> Route.notarizedPut(
    info: MethodInfo,
    noinline body: PipelineInterceptor<Unit, ApplicationCall>
  ): Route {
    println(objectSchema(TQ::class))
    println(objectSchema(TP::class))
    println(objectSchema(TR::class))
    val path = calculatePath()
    openApiSpec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    openApiSpec.paths[path]?.put = OpenApiSpecPathItemOperation(
      summary = info.summary,
      description = info.description,
      tags = info.tags
    )
    return method(HttpMethod.Put) { handle(body) }
  }
}
