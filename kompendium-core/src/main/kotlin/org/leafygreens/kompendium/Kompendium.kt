package org.leafygreens.kompendium

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.util.InternalAPI
import io.ktor.util.pipeline.PipelineInterceptor
import org.leafygreens.kompendium.models.OpenApiSpec
import org.leafygreens.kompendium.models.OpenApiSpecInfo
import org.leafygreens.kompendium.models.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.OpenApiSpecPathItemOperation

object Kompendium {
  val spec = OpenApiSpec(
    info = OpenApiSpecInfo(),
    servers = mutableListOf(),
    paths = mutableMapOf()
  )

  fun Route.notarizedGet(body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    val path = calculatePath()
    spec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    spec.paths[path]?.get = OpenApiSpecPathItemOperation(tags = setOf("test"))
    return method(HttpMethod.Get) { handle(body) }
  }

  fun Route.notarizedPost(body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    val path = calculatePath()
    spec.paths.getOrPut(path) { OpenApiSpecPathItem() }
    spec.paths[path]?.post = OpenApiSpecPathItemOperation(tags = setOf("test"))
    return method(HttpMethod.Post) { handle(body) }
  }

  @OptIn(InternalAPI::class)
  private fun Route.calculatePath(tail: String = ""): String = when (selector) {
    is RootRouteSelector -> tail
    is PathSegmentParameterRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/{$selector}$tail"
    is PathSegmentConstantRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/$selector$tail"
    else -> error("unknown selector type $selector")
  }
}
