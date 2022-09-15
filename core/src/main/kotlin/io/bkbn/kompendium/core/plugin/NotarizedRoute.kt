package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.util.Helpers.addToSpec
import io.bkbn.kompendium.core.util.SpecConfig
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Hook
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.routing.Route

object NotarizedRoute {

  class Config : SpecConfig {
    override var tags: Set<String> = emptySet()
    override var parameters: List<Parameter> = emptyList()
    override var get: GetInfo? = null
    override var post: PostInfo? = null
    override var put: PutInfo? = null
    override var delete: DeleteInfo? = null
    override var patch: PatchInfo? = null
    override var head: HeadInfo? = null
    override var options: OptionsInfo? = null
    override var security: Map<String, List<String>>? = null
    internal var path: Path? = null
  }

  private object InstallHook : Hook<(ApplicationCallPipeline) -> Unit> {
    override fun install(pipeline: ApplicationCallPipeline, handler: (ApplicationCallPipeline) -> Unit) {
      handler(pipeline)
    }
  }

  operator fun invoke() = createRouteScopedPlugin(
    name = "NotarizedRoute",
    createConfiguration = ::Config
  ) {

    // This is required in order to introspect the route path and authentication
    on(InstallHook) {
      val route = it as? Route ?: return@on
      val spec = application.attributes[KompendiumAttributes.openApiSpec]
      val routePath = route.calculateRoutePath()
      val authMethods = route.collectAuthMethods()
      pluginConfig.path?.addDefaultAuthMethods(authMethods)
      require(spec.paths[routePath] == null) {
        """
          The specified path ${Parameter.Location.path} has already been documented!
          Please make sure that all notarized paths are unique
        """.trimIndent()
      }
      spec.paths[routePath] = pluginConfig.path
        ?: error("This indicates a bug in Kompendium. Please file a GitHub issue!")
    }

    val spec = application.attributes[KompendiumAttributes.openApiSpec]
    val serializableReader = application.attributes[KompendiumAttributes.schemaConfigurator]

    val path = Path()
    path.parameters = pluginConfig.parameters

    pluginConfig.get?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.delete?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.head?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.options?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.post?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.put?.addToSpec(path, spec, pluginConfig, serializableReader)
    pluginConfig.patch?.addToSpec(path, spec, pluginConfig, serializableReader)

    pluginConfig.path = path
  }

  private fun Route.calculateRoutePath() = toString().replace(Regex("/\\(.+\\)"), "")
  private fun Route.collectAuthMethods() = toString()
    .split("/")
    .filter { it.contains(Regex("\\(authenticate .*\\)")) }
    .map { it.replace("(authenticate ", "").replace(")", "") }
    .map { it.split(", ") }
    .flatten()

  private fun Path.addDefaultAuthMethods(methods: List<String>) {
    get?.addDefaultAuthMethods(methods)
    put?.addDefaultAuthMethods(methods)
    post?.addDefaultAuthMethods(methods)
    delete?.addDefaultAuthMethods(methods)
    options?.addDefaultAuthMethods(methods)
    head?.addDefaultAuthMethods(methods)
    patch?.addDefaultAuthMethods(methods)
    trace?.addDefaultAuthMethods(methods)
  }

  private fun PathOperation.addDefaultAuthMethods(methods: List<String>) {
    methods.forEach { m ->
      if (security == null || security?.all { s -> !s.containsKey(m) } == true) {
        if (security == null) {
          security = mutableListOf(mapOf(m to emptyList()))
        } else {
          security?.add(mapOf(m to emptyList()))
        }
      }
    }
  }
}
