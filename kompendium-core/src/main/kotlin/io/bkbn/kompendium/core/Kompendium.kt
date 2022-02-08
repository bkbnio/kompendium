package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.schema.TypedSchema
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

class Kompendium(val config: Configuration) {

  class Configuration {
    lateinit var spec: OpenApiSpec

    var cache: SchemaMap = mutableMapOf()
    var specRoute = "/openapi.json"

    // TODO Add tests for this!!
    fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
      cache[clazz.simpleName!!] = schema
    }
  }

  companion object Feature : ApplicationFeature<Application, Configuration, Kompendium> {
    override val key: AttributeKey<Kompendium> = AttributeKey("Kompendium")
    override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kompendium {
      val configuration = Configuration().apply(configure)

      pipeline.intercept(ApplicationCallPipeline.Call) {
        if (call.request.path() == configuration.specRoute) {
          call.respond(HttpStatusCode.OK, configuration.spec)
        }
      }

      return Kompendium(configuration)
    }
  }
}
