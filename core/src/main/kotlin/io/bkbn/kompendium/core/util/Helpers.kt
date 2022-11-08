package io.bkbn.kompendium.core.util

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
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType

object Helpers {

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

  fun MethodInfo.addToSpec(
    path: Path,
    spec: OpenApiSpec,
    config: SpecConfig,
    schemaConfigurator: SchemaConfigurator,
    routePath: String,
    authMethods: List<String> = emptyList()
  ) {
    SchemaGenerator.fromTypeOrUnit(
      this.response.responseType,
      spec.components.schemas, schemaConfigurator
    )?.let { schema ->
      spec.components.schemas[this.response.responseType.getSimpleSlug()] = schema
    }

    errors.forEach { error ->
      SchemaGenerator.fromTypeOrUnit(error.responseType, spec.components.schemas, schemaConfigurator)?.let { schema ->
        spec.components.schemas[error.responseType.getSimpleSlug()] = schema
      }
    }

    when (this) {
      is MethodInfoWithRequest -> {
        SchemaGenerator.fromTypeOrUnit(
          this.request.requestType,
          spec.components.schemas,
          schemaConfigurator
        )?.let { schema ->
          spec.components.schemas[this.request.requestType.getSimpleSlug()] = schema
        }
      }

      else -> {}
    }

    val operations = this.toPathOperation(config)
    operations.addDefaultAuthMethods(authMethods)

    fun setOperation(
      property: KMutableProperty1<Path, PathOperation?>
    ) {
      require(property.get(path) == null) {
        "A route has already been registered for path: $routePath and method: ${property.name.uppercase()}"
      }
      property.set(path, operations)
    }

    return when (this) {
      is DeleteInfo -> setOperation(Path::delete)
      is GetInfo -> setOperation(Path::get)
      is HeadInfo -> setOperation(Path::head)
      is PatchInfo -> setOperation(Path::patch)
      is PostInfo -> setOperation(Path::post)
      is PutInfo -> setOperation(Path::put)
      is OptionsInfo -> setOperation(Path::options)
    }
  }

  private fun MethodInfo.toPathOperation(config: SpecConfig) = PathOperation(
    tags = config.tags.plus(this.tags),
    summary = this.summary,
    description = this.description,
    externalDocs = this.externalDocumentation,
    operationId = this.operationId,
    deprecated = this.deprecated,
    parameters = this.parameters,
    security = config.security
      ?.map { (k, v) -> k to v }
      ?.map { listOf(it).toMap() }
      ?.toMutableList(),
    requestBody = when (this) {
      is MethodInfoWithRequest -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(this.request.examples, this.request.mediaTypes),
        required = true
      )

      else -> null
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = this.response.responseType.toReferenceContent(this.response.examples, this.response.mediaTypes)
      )
    ).plus(this.errors.toResponseMap())
  )

  private fun List<ResponseInfo>.toResponseMap(): Map<Int, Response> = associate { error ->
    error.responseCode.value to Response(
      description = error.description,
      content = error.responseType.toReferenceContent(error.examples, error.mediaTypes)
    )
  }

  private fun KType.toReferenceContent(
    examples: Map<String, MediaType.Example>?,
    mediaTypes: Set<String>
  ): Map<String, MediaType>? =
    when (this.classifier as KClass<*>) {
      Unit::class -> null
      else -> mediaTypes.associateWith {
        MediaType(
          schema = if (this.isMarkedNullable) OneOfDefinition(
            NullableDefinition(),
            ReferenceDefinition(this.getReferenceSlug())
          ) else ReferenceDefinition(this.getReferenceSlug()),
          examples = examples
        )
      }
    }
}
