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
import io.bkbn.kompendium.json.schema.SchemaGenerator
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
import kotlin.reflect.KType

object Helpers {

  fun MethodInfo.addToSpec(path: Path, spec: OpenApiSpec, config: SpecConfig) {
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
      ?.toList(),
    requestBody = when (this) {
      is MethodInfoWithRequest -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(this.request.examples),
        required = true
      )

      else -> null
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = this.response.responseType.toReferenceContent(this.response.examples)
      )
    ).plus(this.errors.toResponseMap())
  )

  private fun List<ResponseInfo>.toResponseMap(): Map<Int, Response> = associate { error ->
    error.responseCode.value to Response(
      description = error.description,
      content = error.responseType.toReferenceContent(error.examples)
    )
  }

  private fun KType.toReferenceContent(examples: Map<String, MediaType.Example>?): Map<String, MediaType>? =
    when (this.classifier as KClass<*>) {
      Unit::class -> null
      else -> mapOf(
        "application/json" to MediaType(
          schema = ReferenceDefinition(this.getReferenceSlug()),
          examples = examples
        )
      )
    }
}
