package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.defaultAuthConfig() {
  authenticate("basic") {
    route(rootPath) {
      basicGetGenerator<TestResponse>()
    }
  }
}

fun Route.customAuthConfig() {
  authenticate("auth-oauth-google") {
    route(rootPath) {
      install(NotarizedRoute()) {
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
          security = mapOf(
            "auth-oauth-google" to listOf("read:pets")
          )
        }
      }
    }
  }
}

fun Route.customScopesOnSiblingPathOperations() {
  authenticate("auth-oauth-google") {
    route(rootPath) {
      install(NotarizedRoute()) {
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
          security = mapOf(
            "auth-oauth-google" to listOf("read:pets")
          )
        }
        post = PostInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
          request {
            description(defaultResponseDescription)
            requestType<TestResponse>()
          }
          security = mapOf(
            "auth-oauth-google" to listOf("write:pets")
          )
        }
      }
    }
  }
}

fun Route.multipleAuthStrategies() {
  authenticate("jwt", "api-key") {
    route(rootPath) {
      basicGetGenerator<TestResponse>()
    }
  }
}
