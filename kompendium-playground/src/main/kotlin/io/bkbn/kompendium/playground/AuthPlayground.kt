package io.bkbn.kompendium.playground

import io.bkbn.kompendium.auth.Notarized.notarizedAuthenticate
import io.bkbn.kompendium.auth.configuration.BasicAuthConfiguration
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.AuthPlaygroundToC.simpleAuthenticatedGet
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Application entrypoint.  Run this and head on over to `localhost:8081/docs`
 * to see some documented, authenticated routes.
 */
fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

// Application Module
private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(Kompendium) {
    spec = Util.baseSpec
  }
  install(Authentication) {
    // We can leverage the security config name to prevent typos
    basic(SecurityConfigurations.basic.name) {
      realm = "Access to the '/' path"
      validate { credentials ->
        if (credentials.name == "admin" && credentials.password == "foobar") {
          UserIdPrincipal(credentials.name)
        } else {
          null
        }
      }

    }
  }
  routing {
    redoc(pageTitle = "Authenticated API")
    notarizedAuthenticate(SecurityConfigurations.basic) {
      notarizedGet(simpleAuthenticatedGet) {
        call.respond(HttpStatusCode.OK, AuthModels.SimpleAuthResponse(true))
      }
    }
  }
}

// This is where we define the available security configurations for our app
object SecurityConfigurations {
  val basic = object : BasicAuthConfiguration {
    override val name: String = "basic"
  }
}

// This is a table of contents to hold all the metadata for our various API endpoints
object AuthPlaygroundToC {
  val simpleAuthenticatedGet = GetInfo<Unit, AuthModels.SimpleAuthResponse>(
    summary = "Simple GET Request behind authentication",
    description = "Can only make this request if you are a true OG",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "Proves that you are in fact an OG"
    ),
    tags = setOf("Authenticated"),
    securitySchemes = setOf(SecurityConfigurations.basic.name)
  )
}

object AuthModels {
  @Serializable
  data class SimpleAuthResponse(val isOG: Boolean)
}
