package io.bkbn.kompendium.playground

import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.FreeFormObject
import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.annotations.constraint.Format
import io.bkbn.kompendium.annotations.constraint.MaxItems
import io.bkbn.kompendium.annotations.constraint.MaxLength
import io.bkbn.kompendium.annotations.constraint.Maximum
import io.bkbn.kompendium.annotations.constraint.MinItems
import io.bkbn.kompendium.annotations.constraint.MinLength
import io.bkbn.kompendium.annotations.constraint.MinProperties
import io.bkbn.kompendium.annotations.constraint.Minimum
import io.bkbn.kompendium.annotations.constraint.MultipleOf
import io.bkbn.kompendium.annotations.constraint.Pattern
import io.bkbn.kompendium.annotations.constraint.UniqueItems
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.Notarized.notarizedPost
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.playground.ConstrainedModels.ConstrainedParams
import io.bkbn.kompendium.playground.ConstrainedModels.ConstrainedRequest
import io.bkbn.kompendium.playground.ConstrainedModels.ConstrainedResponse
import io.bkbn.kompendium.playground.ConstrainedPlaygroundToC.simpleConstrainedGet
import io.bkbn.kompendium.playground.ConstrainedPlaygroundToC.simpleConstrainedPost
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

// Application Module
private fun Application.mainModule() {
  // Installs Simple JSON Content Negotiation
  install(ContentNegotiation) {
    json()
  }
  // Installs the Kompendium Plugin and sets up baseline server metadata
  install(Kompendium) {
    spec = Util.baseSpec
  }
  // Configures the routes for our API
  routing {
    // This adds ReDoc support at the `/docs` endpoint.
    // By default, it will point at the `/openapi.json` created by Kompendium
    redoc(pageTitle = "Constrained API Docs")
    notarizedGet(simpleConstrainedGet) {
      call.respond(HttpStatusCode.OK, ConstrainedResponse(100))
    }
    notarizedPost(simpleConstrainedPost) {
      call.respond(HttpStatusCode.OK, ConstrainedResponse(100))
    }
  }
}

object ConstrainedPlaygroundToC {
  val simpleConstrainedGet = GetInfo<ConstrainedParams, ConstrainedResponse>(
    summary = "Simple, Constrained get",
    description = "Shows that you can set constraints on given fields",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "Cool stuff",
      examples = mapOf("demo" to ConstrainedResponse())
    )
  )
  val simpleConstrainedPost =
    PostInfo<ConstrainedParams, ConstrainedRequest, ConstrainedResponse>(
      summary = "Simple, Constrained post",
      description = "Shows that you can set constraints on given fields",
      requestInfo = RequestInfo(
        description = "Cool stuff"
      ),
      responseInfo = ResponseInfo(
        status = HttpStatusCode.OK,
        description = "Cool stuff",
        examples = mapOf("demo" to ConstrainedResponse())
      )
    )
}

object ConstrainedModels {
  @Serializable
  data class ConstrainedResponse(
    @Minimum("5")
    @Maximum("101")
    @MultipleOf("5")
    val a: Int = 10,
    @MinItems(100)
    @MaxItems(1000)
    @UniqueItems
    val b: List<String> = (0..500).map { it.toString() }
  )

  @Serializable
  data class ConstrainedParams(
    @Field(description = "This is a really important field!")
    @Param(ParamType.QUERY)
    @MinLength(11)
    @MaxLength(11)
    @Pattern("^\\d{3}-\\d{2}-\\d{4}\$")
    @Format("password")
    val ssn: String = "111-11-1111"
  )

  @Serializable
  data class ConstrainedRequest(
    val fieldy: Field,
    @MinProperties(1)
    @FreeFormObject
    val data: JsonElement? = null
  ) {
    @Serializable
    data class Field(val nesty: Nested?) {
      @Serializable
      data class Nested(val a: Int = 100, val b: Boolean)
    }
  }
}
