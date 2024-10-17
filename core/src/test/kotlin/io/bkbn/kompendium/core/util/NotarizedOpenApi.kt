package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.SomethingSimilar
import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestEnum
import io.bkbn.kompendium.core.fixtures.TestRequest
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.util.TestModules.defaultParams
import io.bkbn.kompendium.core.util.TestModules.defaultPath
import io.bkbn.kompendium.core.util.TestModules.defaultPathDescription
import io.bkbn.kompendium.core.util.TestModules.defaultPathSummary
import io.bkbn.kompendium.core.util.TestModules.defaultResponseDescription
import io.bkbn.kompendium.core.util.TestModules.rootPath
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Header
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.notarizedGet() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      get = GetInfo.builder {
        response {
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          description(defaultResponseDescription)
        }
        summary(defaultPathSummary)
        description(defaultPathDescription)
      }
    }
    get {
      call.respondText { "hey dude ‼️ congrats on the get request" }
    }
  }
}

fun Route.responseHeaders() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      get = GetInfo.builder {
        response {
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          description(defaultResponseDescription)
          responseHeaders(
            mapOf(
              HttpHeaders.ETag to Header(
                TypeDefinition.STRING,
                "https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/ETag"
              ),
              HttpHeaders.LastModified to Header(
                TypeDefinition.STRING,
                "https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Last-Modified"
              ),
            )
          )
        }
        summary(defaultPathSummary)
        description(defaultPathDescription)
      }
    }
    get {
      call.respondText { "hey dude ‼️ congrats on the get request" }
    }
  }
}

fun Route.notarizedPost() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      post = PostInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          requestType<TestSimpleRequest>()
          description("A Test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(defaultResponseDescription)
        }
      }
    }
    post {
      call.respondText { "hey dude ‼️ congrats on the post request" }
    }
  }
}

fun Route.notarizedPut() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      put = PutInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          requestType<TestSimpleRequest>()
          description("A Test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(defaultResponseDescription)
        }
      }
    }
    put {
      call.respondText { "hey dude ‼️ congrats on the post request" }
    }
  }
}

fun Route.notarizedDelete() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      delete = DeleteInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          responseCode(HttpStatusCode.NoContent)
          responseType<Unit>()
          description(defaultResponseDescription)
        }
      }
    }
  }
  delete {
    call.respond(HttpStatusCode.NoContent)
  }
}

fun Route.notarizedPatch() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      patch = PatchInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          description("A Test request")
          requestType<TestSimpleRequest>()
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(defaultResponseDescription)
        }
      }
    }
    patch {
      call.respond(HttpStatusCode.Created) { TestCreatedResponse(123, "Nice!") }
    }
  }
}

fun Route.notarizedHead() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      head = HeadInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)

        response {
          description("great!")
          responseCode(HttpStatusCode.Created)
          responseType<Unit>()
        }
      }
    }
    head {
      call.respond(HttpStatusCode.OK)
    }
  }
}

fun Route.notarizedOptions() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      options = OptionsInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          responseCode(HttpStatusCode.OK)
          responseType<TestResponse>()
          description("nice")
        }
      }
    }
    options {
      call.respond(HttpStatusCode.NoContent)
    }
  }
}

fun Route.complexRequest() {
  route(rootPath) {
    install(NotarizedRoute()) {
      put = PutInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          requestType<ComplexRequest>()
          description("A Complex request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(defaultResponseDescription)
        }
      }
    }
    patch {
      call.respond(HttpStatusCode.Created, TestCreatedResponse(123, "nice!"))
    }
  }
}

fun Route.primitives() {
  route(rootPath) {
    install(NotarizedRoute()) {
      put = PutInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          requestType<Int>()
          description("A Test Request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<Boolean>()
          description(defaultResponseDescription)
        }
      }
    }
  }
}

fun Route.returnsList() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      get = GetInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          description("A Successful List-y Endeavor")
          responseCode(HttpStatusCode.OK)
          responseType<List<TestResponse>>()
        }
      }
    }
  }
}

fun Route.returnsEnumList() {
  route(defaultPath) {
    install(NotarizedRoute()) {
      parameters = defaultParams
      get = GetInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          description("A Successful List-y Endeavor")
          responseCode(HttpStatusCode.OK)
          responseType<List<TestEnum>>()
        }
      }
    }
  }
}

fun Route.nonRequiredParams() {
  route("/optional") {
    install(NotarizedRoute()) {
      parameters = listOf(
        Parameter(
          name = "notRequired",
          `in` = Parameter.Location.query,
          schema = TypeDefinition.STRING,
          required = false,
        ),
        Parameter(
          name = "required",
          `in` = Parameter.Location.query,
          schema = TypeDefinition.STRING
        )
      )
      get = GetInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          responseType<Unit>()
          description("Empty")
          responseCode(HttpStatusCode.NoContent)
        }
      }
    }
  }
}

fun Route.overrideMediaTypes() {
  route("/media_types") {
    install(NotarizedRoute()) {
      put = PutInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          mediaTypes("multipart/form-data", "application/json")
          requestType<TestRequest>()
          description("A cool request")
        }
        response {
          mediaTypes("application/xml")
          responseType<TestResponse>()
          description("A good response")
          responseCode(HttpStatusCode.Created)
        }
      }
    }
  }
}

fun Route.postNoReqBody() {
  route("/no_req_body") {
    install(NotarizedRoute()) {
      post = PostInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        response {
          responseType<TestResponse>()
          description("Cool response")
          responseCode(HttpStatusCode.Created)
        }
      }
    }
  }
}

fun Route.fieldOutsideConstructor() {
  route("/field_outside_constructor") {
    install(NotarizedRoute()) {
      post = PostInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        request {
          requestType<SomethingSimilar>()
          description("A cool request")
        }
        response {
          responseType<TestResponse>()
          description("Cool response")
          responseCode(HttpStatusCode.Created)
        }
      }
    }
  }
}
