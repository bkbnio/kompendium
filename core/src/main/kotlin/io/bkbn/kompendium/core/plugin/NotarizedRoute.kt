package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.MethodInfoWithRequest
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Hook
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.routing.Route
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("MagicNumber") // 🚨 obviously remove
object NotarizedRoute {

  class Config {
    var tags: Set<String> = emptySet()
    var parameters: List<Parameter> = emptyList()
    var get: GetInfo? = null
    var post: PostInfo? = null
    var put: PutInfo? = null
    var delete: DeleteInfo? = null
    var patch: PatchInfo? = null
    var head: HeadInfo? = null
    var options: OptionsInfo? = null
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

    // This is required in order to introspect the route path
    on(InstallHook) {
      val route = it as? Route ?: return@on
      val spec = application.attributes[KompendiumAttributes.openApiSpec]
      val routePath = route.calculateRoutePath()
      require(spec.paths[routePath] == null) {
        """
        The specified path ${Parameter.Location.path} has already been documented!
        Please make sure that all notarized paths are unique
      """.trimIndent()
      }
      spec.paths[routePath] = pluginConfig.path!!
    }

    val spec = application.attributes[KompendiumAttributes.openApiSpec]

    val path = Path()
    path.parameters = pluginConfig.parameters

    pluginConfig.get?.addToSpec(path, spec, pluginConfig)
    pluginConfig.delete?.addToSpec(path, spec, pluginConfig)
    pluginConfig.head?.addToSpec(path, spec, pluginConfig)
    pluginConfig.options?.addToSpec(path, spec, pluginConfig)
    pluginConfig.post?.addToSpec(path, spec, pluginConfig)
    pluginConfig.put?.addToSpec(path, spec, pluginConfig)
    pluginConfig.patch?.addToSpec(path, spec, pluginConfig)

    pluginConfig.path = path
  }

  private fun MethodInfo.addToSpec(path: Path, spec: OpenApiSpec, config: Config) {
    SchemaGenerator.fromTypeOrUnit(this.response.responseType, spec.components.schemas)?.let { schema ->
      spec.components.schemas[this.response.responseType.getSimpleSlug()] = schema
    }

    errors.forEach { error ->
      SchemaGenerator.fromTypeOrUnit(error.responseType, spec.components.schemas)?.let { schema ->
        spec.components.schemas[error.responseType.getSimpleSlug()] = schema
      }
    }

    when (this) {
      is MethodInfoWithRequest -> {
        SchemaGenerator.fromTypeOrUnit(this.request.requestType, spec.components.schemas)?.let { schema ->
          spec.components.schemas[this.request.requestType.getSimpleSlug()] = schema
        }
      }
      else -> {}
    }

    val operations = this.toPathOperation(config)

    when (this) {
      is DeleteInfo -> path.delete = operations
      is GetInfo -> path.get = operations
      is HeadInfo -> path.head = operations
      is PatchInfo -> path.patch = operations
      is PostInfo -> path.post = operations
      is PutInfo -> path.put = operations
      is OptionsInfo -> path.options = operations
    }
  }

  private fun MethodInfo.toPathOperation(config: Config) = PathOperation(
    tags = config.tags.plus(this.tags),
    summary = this.summary,
    description = this.description,
    externalDocs = this.externalDocumentation,
    operationId = this.operationId,
    deprecated = this.deprecated,
    parameters = this.parameters,
    requestBody = when (this) {
      is MethodInfoWithRequest -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
      else -> null
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = this.response.responseType.toReferenceContent()
      )
    ).plus(this.errors.toResponseMap())
  )

  private fun List<ResponseInfo>.toResponseMap(): Map<Int, Response> = associate { error ->
    error.responseCode.value to Response(
      description = error.description,
      content = error.responseType.toReferenceContent()
    )
  }

  private fun KType.toReferenceContent(): Map<String, MediaType>? = when (this.classifier as KClass<*>) {
    Unit::class -> null
    else -> mapOf(
      "application/json" to MediaType(
        schema = ReferenceDefinition(this.getReferenceSlug())
      )
    )
  }

  private fun Route.calculateRoutePath() = toString().replace(Regex("/\\(.+\\)"), "")
}
