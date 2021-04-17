package org.leafygreens.kompendium.playground

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.net.URI
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import org.leafygreens.kompendium.Kompendium.notarizedDelete
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.Kompendium.notarizedPost
import org.leafygreens.kompendium.Kompendium.notarizedPut
import org.leafygreens.kompendium.Kompendium.openApiSpec
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.playground.KompendiumTOC.testIdGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleDeleteInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePostInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePutInfo
import org.leafygreens.kompendium.util.KompendiumHttpCodes

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
  routing {
    openApi()
    redoc()
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
        notarizedDelete<Unit, Unit>(testSingleDeleteInfo) {
          call.respondText { "heya" }
        }
      }
    }
  }
}

data class ExampleParams(val a: String, val aa: Int)

data class ExampleNested(val nesty: String)

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

data class ExampleResponse(val c: String)

data class ExampleCreatedResponse(val id: Int, val c: String)

object KompendiumTOC {
  val testIdGetInfo = MethodInfo(
    summary = "Get Test",
    description = "Test for the getting",
    tags = setOf("test", "sample", "get"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns sample info"
    )
  )
  val testSingleGetInfo = MethodInfo(
    summary = "Another get test",
    description = "testing more",
    tags = setOf("anotherTest", "sample"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns a different sample"
    )
  )
  val testSinglePostInfo = MethodInfo(
    summary = "Test post endpoint",
    description = "Post your tests here!",
    requestInfo = RequestInfo(
      description = "Simple request body"
    ),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.CREATED,
      description = "Worlds most complex response"
    )
  )
  val testSinglePutInfo = MethodInfo(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    requestInfo = RequestInfo(
      description = "Info needed to perform this put request"
    ),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.CREATED,
      description = "What we give you when u do the puts"
    )
  )
  val testSingleDeleteInfo = MethodInfo(
    summary = "Test delete endpoint",
    description = "testing my deletes",
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.NO_CONTENT,
      description = "Signifies that your item was deleted successfully",
      mediaTypes = emptyList()
    )
  )
}

fun Routing.openApi() {
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

fun Routing.redoc() {
  route("/docs") {
    get {
      call.respondHtml {
        head {
          title {
            +"${openApiSpec.info.title}"
          }
          meta {
            charset = "utf-8"
          }
          meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
          }
          link {
            href = "https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700"
            rel = "stylesheet"
          }
          style {
            unsafe {
              raw("body { margin: 0; padding: 0; }")
            }
          }
        }
        body {
          // TODO needs to mirror openApi route
          unsafe { +"<redoc spec-url='/openapi.json'></redoc>" }
          script {
            src = "https://cdn.jsdelivr.net/npm/redoc@next/bundles/redoc.standalone.js"
          }
        }
      }
    }
  }
}
