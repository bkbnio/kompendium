package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
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

    pluginConfig.get?.let { get ->
      SchemaGenerator.fromTypeOrUnit(get.response.responseType)?.let { schema ->
        spec.components.schemas[get.response.responseType.getSimpleSlug()] = schema
      }
      path.get = get.toPathOperation(pluginConfig)
    }

    pluginConfig.delete?.let { delete ->
      SchemaGenerator.fromTypeOrUnit(delete.response.responseType)?.let { schema ->
        spec.components.schemas[delete.response.responseType.getSimpleSlug()] = schema
      }

      path.delete = delete.toPathOperation(pluginConfig)
    }

    pluginConfig.head?.let { head ->
      SchemaGenerator.fromTypeOrUnit(head.response.responseType)?.let { schema ->
        spec.components.schemas[head.response.responseType.getSimpleSlug()] = schema
      }

      path.head = head.toPathOperation(pluginConfig)
    }

    pluginConfig.options?.let { options ->
      SchemaGenerator.fromTypeOrUnit(options.response.responseType)?.let { schema ->
        spec.components.schemas[options.response.responseType.getSimpleSlug()] = schema
      }
      path.options = options.toPathOperation(pluginConfig)
    }

    pluginConfig.post?.let { post ->
      SchemaGenerator.fromTypeOrUnit(post.response.responseType)?.let { schema ->
        spec.components.schemas[post.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromTypeOrUnit(post.request.requestType)?.let { schema ->
        spec.components.schemas[post.request.requestType.getSimpleSlug()] = schema
      }
      path.post = post.toPathOperation(pluginConfig)
    }

    pluginConfig.put?.let { put ->
      SchemaGenerator.fromTypeOrUnit(put.response.responseType)?.let { schema ->
        spec.components.schemas[put.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromTypeOrUnit(put.request.requestType)?.let { schema ->
        spec.components.schemas[put.request.requestType.getSimpleSlug()] = schema
      }
      path.put = put.toPathOperation(pluginConfig)
    }

    pluginConfig.patch?.let { patch ->
      SchemaGenerator.fromTypeOrUnit(patch.response.responseType)?.let { schema ->
        spec.components.schemas[patch.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromTypeOrUnit(patch.request.requestType)?.let { schema ->
        spec.components.schemas[patch.request.requestType.getSimpleSlug()] = schema
      }
      path.patch = patch.toPathOperation(pluginConfig)
    }

    pluginConfig.path = path
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
      is DeleteInfo -> null
      is GetInfo -> null
      is HeadInfo -> null
      is OptionsInfo -> null
      is PatchInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
      is PostInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
      is PutInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = this.response.responseType.toReferenceContent()
      )
    )
  )

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
