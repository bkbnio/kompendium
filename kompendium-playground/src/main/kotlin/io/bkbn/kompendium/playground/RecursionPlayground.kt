package io.bkbn.kompendium.playground

import io.bkbn.kompendium.annotations.Referenced
import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.Notarized.notarizedGet
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.routes.redoc
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

enum class ColumnMode {
  NULLABLE,
  REQUIRED,
  REPEATED
}

@Referenced
@Serializable
data class ColumnSchema(
  val name: String,
  val type: String,
  val description: String,
  val mode: ColumnMode,
  val subColumns: List<ColumnSchema> = emptyList()
)

fun main() {
  embeddedServer(
    Netty,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(json = Util.kotlinxConfig)
  }
  install(Kompendium) {
    spec = Util.baseSpec
  }
  routing {
    redoc(pageTitle = "Recursive API Docs")
    notarizedGet(
      GetInfo<Unit, ColumnSchema>(
        summary = "Its recursive",
        description = "This is how we do it!",
        responseInfo = ResponseInfo(
          status = HttpStatusCode.OK,
          description = "This means everything went as expected!",
        ),
        tags = setOf("Simple")
      )
    ) {
      call.respond(HttpStatusCode.OK, "Nice!")
    }
  }
}
