package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.*
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

object TestModules {
  private const val defaultPath = "/test/{a}"
  private const val rootPath = "/"
  private const val defaultResponseDescription = "A Successful Endeavor"
  private const val defaultRequestDescription = "You gotta send it"
  private const val defaultPathSummary = "Great Summary!"
  private const val defaultPathDescription = "testing more"

  private val defaultParams = listOf(
    Parameter(
      name = "a",
      `in` = Parameter.Location.path,
      schema = TypeDefinition.STRING,
    ),
    Parameter(
      name = "aa",
      `in` = Parameter.Location.query,
      schema = TypeDefinition.INT
    )
  )

  fun Routing.notarizedGet() {
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

  fun Routing.notarizedPost() {
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

  fun Routing.notarizedPut() {
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

  fun Routing.notarizedDelete() {
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

  fun Routing.notarizedPatch() {
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

  fun Routing.notarizedHead() {
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

  fun Routing.notarizedOptions() {
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

  fun Routing.complexRequest() {
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

  fun Routing.primitives() {
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

  fun Routing.returnsList() {
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

  fun Routing.nonRequiredParams() {
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

  fun Routing.simplePathParsing() {
    route("/this") {
      route("/is") {
        route("/a") {
          route("/complex") {
            route("path") {
              route("with/an/{id}") {
                install(NotarizedRoute()) {
                  get = GetInfo.builder {
                    parameters = listOf(
                      Parameter(
                        name = "id",
                        `in` = Parameter.Location.path,
                        schema = TypeDefinition.STRING
                      )
                    )
                    summary(defaultPathSummary)
                    description(defaultPathDescription)
                    response {
                      description(defaultResponseDescription)
                      responseCode(HttpStatusCode.OK)
                      responseType<TestResponse>()
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  fun Routing.rootRoute() {
    route(rootPath) {
      install(NotarizedRoute()) {
        parameters = listOf(defaultParams.last())
        get = GetInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
          }
        }
      }
    }
  }

  fun Routing.nestedUnderRoot() {
    route("/") {
      route("/testerino") {
        install(NotarizedRoute()) {
          get = GetInfo.builder {
            summary(defaultPathSummary)
            description(defaultPathDescription)
            response {
              description(defaultResponseDescription)
              responseCode(HttpStatusCode.OK)
              responseType<TestResponse>()
            }
          }
        }
      }
    }
  }

  fun Routing.trailingSlash() {
    route("/test") {
      route("/") {
        install(NotarizedRoute()) {
          get = GetInfo.builder {
            summary(defaultPathSummary)
            description(defaultPathDescription)
            response {
              description(defaultResponseDescription)
              responseCode(HttpStatusCode.OK)
              responseType<TestResponse>()
            }
          }
        }
      }
    }
  }

  fun Routing.singleException() {
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

  fun Routing.multipleExceptions() {
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

  fun Routing.polymorphicException() {
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

  fun Routing.genericException() {
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

  fun Routing.reqRespExamples() {
    route(rootPath) {
      install(NotarizedRoute()) {
        post = PostInfo.builder {
          summary(defaultPathSummary)
          description(defaultPathDescription)
          request {
            description(defaultRequestDescription)
            requestType<TestRequest>()
            examples(
              "Testerina" to TestRequest(TestNested("asdf"), 1.5, emptyList())
            )
          }
          response {
            description(defaultResponseDescription)
            responseCode(HttpStatusCode.OK)
            responseType<TestResponse>()
            examples(
              "Testerino" to TestResponse("Heya")
            )
          }
        }
      }
    }
  }

  fun Routing.exampleParams() = basicGetGenerator<TestResponse>(
    params = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING,
        examples = mapOf(
          "foo" to Parameter.Example("testing")
        )
      )
    )
  )

  fun Routing.defaultParameter() = basicGetGenerator<TestResponse>(
    params = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING.withDefault("IDK")
      )
    )
  )

  fun Routing.requiredParams() = basicGetGenerator<TestResponse>(
    params = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      )
    )
  )

  fun Routing.nonRequiredParam() = basicGetGenerator<TestResponse>(
    params = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.query,
        schema = TypeDefinition.STRING,
        required = false
      )
    )
  )

  fun Routing.defaultField() = basicGetGenerator<DefaultField>()

  fun Routing.nullableField() = basicGetGenerator<NullableField>()

  fun Routing.polymorphicResponse() = basicGetGenerator<FlibbityGibbit>()

  fun Routing.ignoredFieldsResponse() = basicGetGenerator<TransientObject>()

  fun Routing.unbackedFieldsResponse() = basicGetGenerator<UnbakcedObject>()

  fun Routing.customFieldNameResponse() = basicGetGenerator<SerialNameObject>()

  fun Routing.polymorphicCollectionResponse() = basicGetGenerator<List<FlibbityGibbit>>()

  fun Routing.polymorphicMapResponse() = basicGetGenerator<Map<String, FlibbityGibbit>>()

  fun Routing.simpleGenericResponse() = basicGetGenerator<Gibbity<String>>()

  fun Routing.gnarlyGenericResponse() = basicGetGenerator<Foosy<Barzo<Int>, String>>()

  fun Routing.nestedGenericResponse() = basicGetGenerator<Gibbity<Map<String, String>>>()

  fun Routing.genericPolymorphicResponse() = basicGetGenerator<Flibbity<Double>>()

  fun Routing.genericPolymorphicResponseMultipleImpls() = basicGetGenerator<Flibbity<FlibbityGibbit>>()

  fun Routing.nestedGenericCollection() = basicGetGenerator<Page<Int>>()

  fun Routing.nestedGenericMultipleParamsCollection() = basicGetGenerator<MultiNestedGenerics<String, ComplexRequest>>()

  fun Routing.withOperationId() = basicGetGenerator<TestResponse>(operationId = "getThisDude")

  fun Routing.nullableNestedObject() = basicGetGenerator<ProfileUpdateRequest>()

  fun Routing.nullableEnumField() = basicGetGenerator<NullableEnum>()

  fun Routing.nullableReference() = basicGetGenerator<ManyThings>()

  fun Routing.dateTimeString() = basicGetGenerator<DateTimeString>()

  fun Routing.headerParameter() = basicGetGenerator<TestResponse>(
    params = listOf(
      Parameter(
        name = "X-User-Email",
        `in` = Parameter.Location.header,
        schema = TypeDefinition.STRING,
        required = true
      )
    )
  )

  fun Routing.nestedTypeName() = basicGetGenerator<Nested.Response>()

  fun Routing.simpleRecursive() = basicGetGenerator<ColumnSchema>()

  fun Routing.defaultAuthConfig() {
    authenticate("basic") {
      route(rootPath) {
        basicGetGenerator<TestResponse>()
      }
    }
  }

  fun Routing.customAuthConfig() {
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

  fun Routing.multipleAuthStrategies() {
    authenticate("jwt", "api-key") {
      route(rootPath) {
        basicGetGenerator<TestResponse>()
      }
    }
  }

  private inline fun <reified T> Routing.basicGetGenerator(
    params: List<Parameter> = emptyList(),
    operationId: String? = null
  ) {
    route(rootPath) {
      basicGetGenerator<T>(params, operationId)
    }
  }

  private inline fun <reified T> Route.basicGetGenerator(
    params: List<Parameter> = emptyList(),
    operationId: String? = null
  ) {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary(defaultPathSummary)
        description(defaultPathDescription)
        operationId?.let { operationId(it) }
        parameters = params
        response {
          description(defaultResponseDescription)
          responseCode(HttpStatusCode.OK)
          responseType<T>()
        }
      }
    }
  }
}
