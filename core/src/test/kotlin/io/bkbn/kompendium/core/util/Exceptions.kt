package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.ExceptionResponse
import io.bkbn.kompendium.core.fixtures.Flibbity
import io.bkbn.kompendium.core.fixtures.FlibbityGibbit
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.singleException() {
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
        canRespond {
          description("Bad Things Happened")
          responseCode(HttpStatusCode.BadRequest)
          responseType<ExceptionResponse>()
        }
      }
    }
  }
}

fun Route.multipleExceptions() {
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
        canRespond {
          description("Bad Things Happened")
          responseCode(HttpStatusCode.BadRequest)
          responseType<ExceptionResponse>()
        }
        canRespond {
          description("Access Denied")
          responseCode(HttpStatusCode.Forbidden)
          responseType<ExceptionResponse>()
        }
      }
    }
  }
}

fun Route.polymorphicException() {
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
        canRespond {
          description("Bad Things Happened")
          responseCode(HttpStatusCode.InternalServerError)
          responseType<FlibbityGibbit>()
        }
      }
    }
  }
}

fun Route.genericException() {
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
        canRespond {
          description("Bad Things Happened")
          responseCode(HttpStatusCode.BadRequest)
          responseType<Flibbity<String>>()
        }
      }
    }
  }
}
