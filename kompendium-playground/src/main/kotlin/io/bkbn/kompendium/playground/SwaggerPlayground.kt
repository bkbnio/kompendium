package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.security.OAuth
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.Util
import io.bkbn.kompendium.swagger.JsConfig
import io.bkbn.kompendium.swagger.SwaggerUI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.URI
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * Application entrypoint.  Run this and head on over to `localhost:8081/docs`
 * to see a very simple yet beautifully documented API
 */
@ExperimentalSerializationApi
fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

const val securitySchemaName = "oauth"

// Application Module
@ExperimentalSerializationApi
private fun Application.mainModule() {
  // Installs Simple JSON Content Negotiation
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = Util.baseSpec.copy(components = Components(securitySchemes = mutableMapOf(
      securitySchemaName to OAuth(description = "OAuth Auth", flows = OAuth.Flows(
        authorizationCode = OAuth.Flows.AuthorizationCode(
          authorizationUrl =  "http://localhost/auth",
          tokenUrl = "http://localhost/token"
        )
      ))
    )))
  }

  install(SwaggerUI) {
      swaggerUrl = "/swagger-ui"
      jsConfig = JsConfig(
        specs = mapOf(
          "My API v1" to URI("/openapi.json"),
          "My API v2" to URI("/openapi.json")
        ),
        // This part will be inserted after Swagger UI is loaded in Browser.
        // Example is prepared according to this documentation: https://swagger.io/docs/open-source-tools/swagger-ui/usage/oauth2/
        jsInit = {
          """
  window.ui.initOAuth({
      clientId: 'CLIENT_ID',
      clientSecret: 'CLIENT_SECRET',
      realm: 'MY REALM',
      appName: 'TEST APP',
      useBasicAuthenticationWithAccessCodeGrant: true
  });  
      """
        }
      )
  }

  // Configures the routes for our API
  routing {
    // Kompendium infers the route path from the Ktor Route.  This will show up as the root path `/`
    notarizedGet(BasicPlaygroundToC.simpleGetExample.copy(securitySchemes = setOf(securitySchemaName))) {
      call.respond(HttpStatusCode.OK, BasicModels.BasicResponse(c = UUID.randomUUID().toString()))
    }
  }
}
