package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.Customization.customSerializer
import io.bkbn.kompendium.playground.SerializerOverridePlaygroundToC.getExample
import io.bkbn.kompendium.playground.util.Util
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(Json {
      encodeDefaults = false
    })
  }
  install(Kompendium) {
    spec = Util.baseSpec
    openApiJson = { spec ->
      route("/openapi.json") {
        get {
          call.respondText { customSerializer.encodeToString(spec) }
        }
      }
    }
  }
  routing {
    swagger(pageTitle = "Docs")
    notarizedGet(getExample) {
      call.respond(HttpStatusCode.OK, SerializerOverrideModel.OhYeaCoolData(null))
    }
  }
}

object SerializerOverridePlaygroundToC {
  val getExample = GetInfo<Unit, SerializerOverrideModel.OhYeaCoolData>(
    summary = "Overriding the serializer",
    description = "Pretty neat!",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "This means everything went as expected!",
      examples = mapOf("demo" to SerializerOverrideModel.OhYeaCoolData(null))
    ),
    tags = setOf("Custom")
  )
}

object SerializerOverrideModel {
  @Serializable
  data class OhYeaCoolData(val num: Int?, val test: String = "gonezo")
}

object Customization {
  val customSerializer = Json {
    serializersModule = KompendiumSerializersModule.module
    encodeDefaults = true
    explicitNulls = false
  }
}
