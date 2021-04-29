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
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.playground.KompendiumTOC.testAuthenticatedSingleGetInfo
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
fun Application.mainModule() {
  // only install once in case of auto reload
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
  routing {
    openApi(oas)
    redoc(oas)
    swaggerUI()
    route("/test") {
      route("/{id}") {
        notarizedGet<ExampleParams, ExampleResponse>(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      route("/single") {
        notarizedGet<Unit, ExampleResponse>(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost<Unit, ExampleRequest, ExampleCreatedResponse>(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut<JustQuery, ExampleRequest, ExampleCreatedResponse>(testSinglePutInfo) {
          call.respondText { "hey" }
        }
        notarizedDelete<Unit, Unit>(testSingleDeleteInfo) {
          call.respondText { "heya" }
        }
      }
      authenticate("basic") {
        route("/authenticated/single") {
          notarizedGet<Unit, Unit>(testAuthenticatedSingleGetInfo) {
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
  val testSingleGetInfoWithThrowable = testSingleGetInfo.copy(
    summary = "Show me the error baby 🙏",
    canThrow = setOf(Exception::class)
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
  val testAuthenticatedSingleGetInfo = MethodInfo(
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
