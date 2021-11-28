package io.bkbn.kompendium.auth.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedBasic
import io.bkbn.kompendium.auth.KompendiumAuth.notarizedJwt
import io.bkbn.kompendium.auth.util.TestData.testGetInfo
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.configModule() {
  install(ContentNegotiation) {
    jackson(ContentType.Application.Json) {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
}

fun Application.configBasicAuth() {
  install(Authentication) {
    notarizedBasic(TestData.AuthConfigName.Basic) {
      realm = "Ktor Server"
      validate { credentials ->
        if (credentials.name == credentials.password) {
          UserIdPrincipal(credentials.name)
        } else {
          null
        }
      }
    }
  }
}

fun Application.configJwtAuth(
  bearerFormat: String? = null
) {
  install(Authentication) {
    notarizedJwt(TestData.AuthConfigName.JWT, bearerFormat) {
      realm = "Ktor server"
    }
  }
}

fun Application.notarizedAuthenticatedGetModule(vararg authenticationConfigName: String) {
  routing {
    authenticate(*authenticationConfigName) {
      route(TestData.getRoutePath) {
        notarizedGet(testGetInfo(*authenticationConfigName)) {
          call.respondText { "hey dude ‼️ congratz on the get request" }
        }
      }
    }
  }
}

fun Application.docs() {
  routing {
    val oas = Kompendium.openApiSpec.copy()
    openApi(oas)
    redoc(oas)
  }
}
