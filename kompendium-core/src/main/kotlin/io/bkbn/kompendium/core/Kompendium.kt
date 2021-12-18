package io.bkbn.kompendium.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.schema.TypedSchema
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

class Kompendium(val config: Configuration) {

  class Configuration {
    lateinit var spec: OpenApiSpec
    var cache: SchemaMap = emptyMap()

    // TODO Document this better!!
    var specRoute = "/openapi.json"

    // TODO Add tests for this!!
    fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
      cache = cache.plus(clazz.simpleName!! to schema)
    }

    // TODO Add tests for this!!
    var om: ObjectMapper = ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .enable(SerializationFeature.INDENT_OUTPUT)

    fun specToJson(): String = om.writeValueAsString(spec)
  }

  companion object Feature : ApplicationFeature<Application, Configuration, Kompendium> {
    override val key: AttributeKey<Kompendium> = AttributeKey("Kompendium")
    override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kompendium {
      val configuration = Configuration().apply(configure)

      pipeline.intercept(ApplicationCallPipeline.Call) {
        if (call.request.path() == configuration.specRoute) {
          call.respondText { configuration.specToJson() }
          call.response.status(HttpStatusCode.OK)
        }
      }

      return Kompendium(configuration)
    }
  }
}
