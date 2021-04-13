package org.leafygreens.kompendium.playground

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.URI
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.Kompendium.notarizedPost
import org.leafygreens.kompendium.Kompendium.notarizedPut
import org.leafygreens.kompendium.Kompendium.openApiSpec
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumResponse
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.playground.KompendiumTOC.testIdGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePostInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePutInfo

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

data class ExampleParams(val a: String, val aa: Int)

data class ExampleNested(val nesty: String)

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

@KompendiumResponse(200, "A Successful Endeavor")
data class ExampleResponse(val c: String)

@KompendiumResponse(201, "Created Successfully")
data class ExampleCreatedResponse(val id: Int, val c: String)

object KompendiumTOC {
  val testIdGetInfo = MethodInfo("Get Test", "Test for getting", tags = setOf("test", "example", "get"))
  val testSingleGetInfo = MethodInfo("Another get test", "testing more")
  val testSinglePostInfo = MethodInfo("Test post endpoint", "Post your tests here!")
  val testSinglePutInfo = MethodInfo("Test put endpoint", "Put your tests here!")
}

fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson()
  }
  routing {
    route("/test") {
      route("/{id}") {
        notarizedGet<ExampleParams, ExampleResponse>(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet<ExampleRequest, ExampleResponse>(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost<ExampleParams, ExampleRequest, ExampleCreatedResponse>(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut<ExampleParams, ExampleRequest, ExampleCreatedResponse>(testSinglePutInfo) {
          call.respondText { "hey" }
        }
      }
    }
    route("/openapi.json") {
      get {
        call.respond(
          openApiSpec.copy(
            info = OpenApiSpecInfo(
              title = "Test API",
              version = "1.33.7",
              description = "An amazing, fully-ish 😉 generated API spec",
              termsOfService = URI("https://example.com"),
              contact = OpenApiSpecInfoContact(
                name = "Homer Simpson",
                email = "chunkylover53@aol.com",
                url = URI("https://gph.is/1NPUDiM")
              ),
              license = OpenApiSpecInfoLicense(
                name = "MIT",
                url = URI("https://github.com/lg-backbone/kompendium/blob/main/LICENSE")
              )
            ),
            servers = mutableListOf(
              OpenApiSpecServer(
                url = URI("https://myawesomeapi.com"),
                description = "Production instance of my API"
              ),
              OpenApiSpecServer(
                url = URI("https://staging.myawesomeapi.com"),
                description = "Where the fun stuff happens"
              )
            )
          )
        )
      }
    }
  }
}
