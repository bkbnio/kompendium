package io.bkbn.kompendium.auth.util

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

//import io.bkbn.kompendium.auth.Notarized.notarizedAuthenticate
//import io.bkbn.kompendium.auth.configuration.SecurityConfiguration
//import io.bkbn.kompendium.core.Notarized.notarizedGet
//import io.bkbn.kompendium.core.fixtures.TestParams
//import io.bkbn.kompendium.core.fixtures.TestResponse
//import io.bkbn.kompendium.core.fixtures.TestResponseInfo
//import io.bkbn.kompendium.core.metadata.method.GetInfo
//import io.ktor.application.Application
//import io.ktor.application.call
//import io.ktor.application.install
//import io.ktor.auth.Authentication
//import io.ktor.auth.OAuthServerSettings
//import io.ktor.auth.UserIdPrincipal
//import io.ktor.auth.basic
//import io.ktor.auth.jwt.jwt
//import io.ktor.auth.oauth
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.http.HttpMethod
//import io.ktor.response.respondText
//import io.ktor.routing.route
//import io.ktor.routing.routing
//
//fun Application.setupOauth() {
//  install(Authentication) {
//    oauth("oauth") {
//      urlProvider = { "http://localhost:8080/callback" }
//      client = HttpClient(CIO)
//      providerLookup = {
//        OAuthServerSettings.OAuth2ServerSettings(
//          name = "google",
//          authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//          accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//          requestMethod = HttpMethod.Post,
//          clientId = System.getenv("GOOGLE_CLIENT_ID"),
//          clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
//          defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
//        )
//      }
//    }
//  }
//}
//
//fun Application.configBasicAuth() {
//  install(Authentication) {
//    basic(AuthConfigName.Basic) {
//      realm = "Ktor Server"
//      validate { credentials ->
//        if (credentials.name == credentials.password) {
//          UserIdPrincipal(credentials.name)
//        } else {
//          null
//        }
//      }
//    }
//  }
//}

fun Application.configBasicAuth() {
  install(Authentication) {
    basic(AuthConfigName.Basic) {
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

//
//fun Application.notarizedAuthRoute(authConfig: SecurityConfiguration) {
//  routing {
//    notarizedAuthenticate(authConfig) {
//      route("/test") { notarizedGet(testGetInfo(authConfig.name)) {
//          call.respondText { "hey dude ‼️ congratz on the get request" }
//        }
//      }
//    }
//  }
//}
//
//fun Application.configJwtAuth() {
//  install(Authentication) {
//    jwt(AuthConfigName.JWT) {
//      realm = "Ktor server"
//    }
//  }
//}
//
//fun testGetInfo(vararg security: String) =
//  GetInfo<TestParams, TestResponse>(
//    summary = "Great Summary!",
//    description = "testing more",
//    responseInfo = TestResponseInfo.testGetResponse,
//    securitySchemes = security.toSet()
//  )
//
object AuthConfigName {
  const val Basic = "basic"
  const val JWT = "jwt"
  const val OAuth = "oauth"
}
