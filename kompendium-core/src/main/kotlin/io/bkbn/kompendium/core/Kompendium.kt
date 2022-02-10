package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.schema.TypedSchema
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

class Kompendium(val config: Configuration) {

  class Configuration {
    lateinit var spec: OpenApiSpec

    var cache: SchemaMap = mutableMapOf()
    var openApiJson: Routing.(OpenApiSpec) -> Unit = { spec ->
      route("/openapi.json") {
        get {
          call.respond(HttpStatusCode.OK, spec)
        }
      }
    }

    // TODO Add tests for this!!
    fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
      cache[clazz.simpleName!!] = schema
    }
  }

  companion object Feature : ApplicationFeature<Application, Configuration, Kompendium> {
    override val key: AttributeKey<Kompendium> = AttributeKey("Kompendium")
    override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kompendium {
      val configuration = Configuration().apply(configure)
      val routing = pipeline.routing { }
      configuration.openApiJson(routing, configuration.spec)
      return Kompendium(configuration)
    }
  }
}
