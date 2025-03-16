package io.bkbn.kompendium.resources

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.plugin.NotarizedRoute.addToSpec
import io.bkbn.kompendium.core.plugin.NotarizedRoute.calculateRoutePath
import io.bkbn.kompendium.core.plugin.NotarizedRoute.collectAuthMethods
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Hook
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.routing.Route

object NotarizedResource : NotarizedMethodResource()
object NotarizedGetResource : NotarizedMethodResource()
object NotarizedPostResource : NotarizedMethodResource()
object NotarizedPutResource : NotarizedMethodResource()
object NotarizedDeleteResource : NotarizedMethodResource()
object NotarizedHeadResource : NotarizedMethodResource()
object NotarizedPatchResource : NotarizedMethodResource()
object NotarizedOptionsResource : NotarizedMethodResource()

abstract class NotarizedMethodResource {
  object InstallHook : Hook<(ApplicationCallPipeline) -> Unit> {
    override fun install(pipeline: ApplicationCallPipeline, handler: (ApplicationCallPipeline) -> Unit) {
      handler(pipeline)
    }
  }

  inline operator fun <reified T> invoke() = createRouteScopedPlugin(
    name = "$this<${T::class.qualifiedName}>",
    createConfiguration = NotarizedRoute::Config
  ) {
    on(InstallHook) {
      val route = it as? Route ?: return@on
      val spec = application.attributes[KompendiumAttributes.openApiSpec]
      val routePath = route.calculateRoutePath()
      val authMethods = route.collectAuthMethods()
      val resourcePath = T::class.getResourcePathFromClass()
      val fullPath = "$routePath$resourcePath"

      addToSpec(spec, fullPath, authMethods)
    }
  }
}
