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
import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.json.schema.util.Helpers.getSlug
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
      type = this.response.responseType,
      cache = spec.components.schemas,
      schemaConfigurator = schemaConfigurator,
      enrichment = this.response.typeEnrichment,
    )?.let { schema ->
      spec.components.schemas[this.response.responseType.getSlug(this.response.typeEnrichment)] = schema
    }

    errors.forEach { error ->
      SchemaGenerator.fromTypeOrUnit(
        type = error.responseType,
        cache = spec.components.schemas,
        schemaConfigurator = schemaConfigurator,
        enrichment = error.typeEnrichment,
      )?.let { schema ->
        spec.components.schemas[error.responseType.getSlug(error.typeEnrichment)] = schema
      }
    }

    when (this) {
      is MethodInfoWithRequest -> {
        this.request?.let { reqInfo ->
          SchemaGenerator.fromTypeOrUnit(
            type = reqInfo.requestType,
            cache = spec.components.schemas,
            schemaConfigurator = schemaConfigurator,
            enrichment = reqInfo.typeEnrichment,
          )?.let { schema ->
            spec.components.schemas[reqInfo.requestType.getSlug(reqInfo.typeEnrichment)] = schema
          }
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
    security = this.createCombinedSecurityContext(config),
    requestBody = when (this) {
      is MethodInfoWithRequest -> this.request?.let { reqInfo ->
        Request(
          description = reqInfo.description,
          content = reqInfo.requestType.toReferenceContent(
            examples = reqInfo.examples,
            mediaTypes = reqInfo.mediaTypes,
            enrichment = reqInfo.typeEnrichment
          ),
          required = reqInfo.required
        )
      }

      else -> null
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        headers = this.response.responseHeaders,
        content = this.response.responseType.toReferenceContent(
          examples = this.response.examples,
          mediaTypes = this.response.mediaTypes,
          enrichment = this.response.typeEnrichment
        )
      )
    ).plus(this.errors.toResponseMap())
  )

  private fun MethodInfo.createCombinedSecurityContext(config: SpecConfig): MutableList<Map<String, List<String>>>? {
    val configSecurity = config.security
      ?.map { (k, v) -> k to v }
      ?.map { listOf(it).toMap() }
      ?.toMutableList()

    val methodSecurity = this.security
      ?.map { (k, v) -> k to v }
      ?.map { listOf(it).toMap() }
      ?.toMutableList()

    return when {
      configSecurity == null && methodSecurity == null -> null
      configSecurity == null -> methodSecurity
      methodSecurity == null -> configSecurity
      else -> configSecurity.plus(methodSecurity).toMutableList()
    }
  }

  private fun List<ResponseInfo>.toResponseMap(): Map<Int, Response> = associate { error ->
    error.responseCode.value to Response(
      description = error.description,
      headers = error.responseHeaders,
      content = error.responseType.toReferenceContent(
        examples = error.examples,
        mediaTypes = error.mediaTypes,
        enrichment = error.typeEnrichment
      )
    )
  }

  private fun KType.toReferenceContent(
    examples: Map<String, MediaType.Example>?,
    mediaTypes: Set<String>,
    enrichment: TypeEnrichment<*>?
  ): Map<String, MediaType>? =
    when (this.classifier as KClass<*>) {
      Unit::class -> null
      else -> mediaTypes.associateWith {
        MediaType(
          schema = if (this.isMarkedNullable) {
            OneOfDefinition(
              NullableDefinition(),
              ReferenceDefinition(this.getReferenceSlug(enrichment))
            )
          } else {
            ReferenceDefinition(this.getReferenceSlug(enrichment))
          },
          examples = examples
        )
      }
    }
}
