package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.json.schema.KotlinXSchemaConfigurator
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.reflect.KType

object NotarizedApplication {

  class Config {
    lateinit var spec: () -> OpenApiSpec
    var specRoute: (OpenApiSpec, Routing) -> Unit = { spec, routing ->
      routing.route("/openapi.json") {
        get {
          call.respond(spec)
        }
      }
    }
    var customTypes: Map<KType, JsonSchema> = emptyMap()
    var schemaConfigurator: SchemaConfigurator = KotlinXSchemaConfigurator()
  }

  operator fun invoke() = createApplicationPlugin(
    name = "NotarizedApplication",
    createConfiguration = ::Config
  ) {
    val spec = pluginConfig.spec()
    val routing = application.routing {}
    this@createApplicationPlugin.pluginConfig.specRoute(spec, routing)
    // pluginConfig.openApiJson(routing)
    pluginConfig.customTypes.forEach { (type, schema) ->
      spec.components.schemas[type.getSimpleSlug()] = schema
    }
    application.attributes.put(KompendiumAttributes.openApiSpec, spec)
    application.attributes.put(KompendiumAttributes.schemaConfigurator, pluginConfig.schemaConfigurator)
  }
}
