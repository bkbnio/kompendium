package io.bkbn.kompendium.playground

//import io.bkbn.kompendium.core.Kompendium
//import io.bkbn.kompendium.core.Notarized.notarizedGet
//import io.bkbn.kompendium.core.metadata.ResponseInfo
//import io.bkbn.kompendium.core.metadata.method.GetInfo
//import io.bkbn.kompendium.core.routes.redoc
//import io.bkbn.kompendium.oas.schema.SimpleSchema
//import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
//import io.bkbn.kompendium.playground.CustomTypePlaygroundToC.simpleGetExample
//import io.bkbn.kompendium.playground.util.Util
//import io.ktor.application.Application
//import io.ktor.application.call
//import io.ktor.application.install
//import io.ktor.features.ContentNegotiation
//import io.ktor.http.HttpStatusCode
//import io.ktor.response.respond
//import io.ktor.routing.routing
//import io.ktor.serialization.json
//import io.ktor.server.engine.embeddedServer
//import io.ktor.server.netty.Netty
//import kotlinx.datetime.Clock
//import kotlinx.datetime.Instant
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//fun main() {
//  embeddedServer(
//    Netty,
//    port = 8081,
//    module = Application::mainModule
//  ).start(wait = true)
//}
//
//private fun Application.mainModule() {
//  install(ContentNegotiation) {
//    json(Json {
//      serializersModule = KompendiumSerializersModule.module
//      encodeDefaults = true
//      explicitNulls = false
//    })
//  }
//  install(Kompendium) {
//    spec = Util.baseSpec
//    // Tells Kompendium how to handle a specific type
//    addCustomTypeSchema(Instant::class, SimpleSchema("string", format = "date-time"))
//  }
//  routing {
//    redoc(pageTitle = "Custom overridden type Docs")
//    notarizedGet(simpleGetExample) {
//      call.respond(HttpStatusCode.OK, CustomTypeModels.AwesomeThingHappened("this http call!", Clock.System.now()))
//    }
//  }
//}
//
//private object CustomTypePlaygroundToC {
//  val simpleGetExample = GetInfo<Unit, CustomTypeModels.AwesomeThingHappened>(
//    summary = "Simple, Documented GET Request",
//    description = "This is to showcase just how easy it is to document your Ktor API!",
//    responseInfo = ResponseInfo(
//      status = HttpStatusCode.OK,
//      description = "This means everything went as expected!",
//    ),
//    tags = setOf("Simple")
//  )
//}
//
//private object CustomTypeModels {
//  @Serializable
//  data class AwesomeThingHappened(
//    val thing: String,
//    val timestamp: Instant
//  )
//}
