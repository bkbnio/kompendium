package org.leafygreens.kompendium.playground

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.webjars.Webjars
import java.net.URI
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.Notarized.notarizedDelete
import org.leafygreens.kompendium.Notarized.notarizedException
import org.leafygreens.kompendium.Notarized.notarizedGet
import org.leafygreens.kompendium.Notarized.notarizedPost
import org.leafygreens.kompendium.Notarized.notarizedPut
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam
import org.leafygreens.kompendium.auth.KompendiumAuth.notarizedBasic
import org.leafygreens.kompendium.models.meta.MethodInfo.GetInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PostInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.PutInfo
import org.leafygreens.kompendium.models.meta.MethodInfo.DeleteInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.playground.KompendiumTOC.testAuthenticatedSingleGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testGetWithExamples
import org.leafygreens.kompendium.playground.KompendiumTOC.testIdGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleDeleteInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleGetInfoWithThrowable
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePostInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePutInfo
import org.leafygreens.kompendium.routes.openApi
import org.leafygreens.kompendium.routes.redoc
import org.leafygreens.kompendium.swagger.swaggerUI
import org.leafygreens.kompendium.util.KompendiumHttpCodes

private val oas = Kompendium.openApiSpec.copy(
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

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

var featuresInstalled = false

fun Application.configModule() {
  if (!featuresInstalled) {
    install(ContentNegotiation) {
      jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
      }
    }
    install(Authentication) {
      notarizedBasic("basic") {
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
    install(Webjars)
    install(StatusPages) {
      notarizedException<Exception, ExceptionResponse>(
        info = ResponseInfo(
          KompendiumHttpCodes.BAD_REQUEST,
          "Bad Things Happened"
        )
      ) {
        call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
      }
    }
    featuresInstalled = true
  }
}

fun Application.mainModule() {
  configModule()
  routing {
    openApi(oas)
    redoc(oas)
    swaggerUI()
    route("/potato/spud") {
      notarizedGet(testGetWithExamples) {
        call.respond(HttpStatusCode.OK)
      }
    }
    route("/test") {
      route("/{id}") {
        notarizedGet(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut(testSinglePutInfo) {
          call.respondText { "hey" }
        }
        notarizedDelete(testSingleDeleteInfo) {
          call.respondText { "heya" }
        }
      }
      authenticate("basic") {
        route("/authenticated/single") {
          notarizedGet(testAuthenticatedSingleGetInfo) {
            call.respond(HttpStatusCode.OK)
          }
        }
      }
    }
    route("/error") {
      notarizedGet<Unit, ExampleResponse>(testSingleGetInfoWithThrowable) {
        error("bad things just happened")
      }
    }
  }
}

data class ExampleParams(
  @PathParam val id: Int,
  @QueryParam val name: String
)

data class JustQuery(
  @QueryParam val potato: Boolean,
  @QueryParam val tomato: String
)

data class ExampleNested(val nesty: String)

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

data class ExampleResponse(val c: String)

data class ExceptionResponse(val message: String)

data class ExampleCreatedResponse(val id: Int, val c: String)

object KompendiumTOC {
  val testGetWithExamples = GetInfo<Unit, ExampleResponse>(
    summary = "Example Parameters",
    description = "A test for setting parameter examples",
    responseInfo = ResponseInfo(
      status = 200,
      description = "nice",
      examples = mapOf("test" to ExampleResponse(c = "spud"))
    ),
  )
  val testIdGetInfo = GetInfo<ExampleParams, ExampleResponse>(
    summary = "Get Test",
    description = "Test for the getting",
    tags = setOf("test", "sample", "get"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns sample info"
    )
  )
  val testSingleGetInfo = GetInfo<Unit, ExampleResponse>(
    summary = "Another get test",
    description = "testing more",
    tags = setOf("anotherTest", "sample"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns a different sample"
    )
  )
  val testSingleGetInfoWithThrowable = testSingleGetInfo.copy(
    summary = "Show me the error baby 🙏",
    canThrow = setOf(Exception::class)
  )
  val testSinglePostInfo = PostInfo<Unit, ExampleRequest, ExampleCreatedResponse>(
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
  val testSinglePutInfo = PutInfo<JustQuery, ExampleRequest, ExampleCreatedResponse>(
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
  val testSingleDeleteInfo = DeleteInfo<Unit, Unit>(
    summary = "Test delete endpoint",
    description = "testing my deletes",
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.NO_CONTENT,
      description = "Signifies that your item was deleted successfully",
      mediaTypes = emptyList()
    )
  )
  val testAuthenticatedSingleGetInfo = GetInfo<Unit, Unit>(
    summary = "Another get test",
    description = "testing more",
    tags = setOf("anotherTest", "sample"),
    responseInfo = ResponseInfo(
      status = KompendiumHttpCodes.OK,
      description = "Returns a different sample"
    ),
    securitySchemes = setOf("basic")
  )
}
