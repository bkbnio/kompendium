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
import org.leafygreens.kompendium.Kompendium.notarizedGet
import org.leafygreens.kompendium.Kompendium.notarizedPost
import org.leafygreens.kompendium.Kompendium.notarizedPut
import org.leafygreens.kompendium.Kompendium.notarizedRoute
import org.leafygreens.kompendium.Kompendium.openApiSpec
import org.leafygreens.kompendium.MethodInfo
import org.leafygreens.kompendium.RouteInfo
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.playground.KompendiumTOC.testIdGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleGetInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePostInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSinglePutInfo
import org.leafygreens.kompendium.playground.KompendiumTOC.testSingleRouteInfo

fun main() {
  embeddedServer(
    Netty,
    port = 8080,
    module = Application::mainModule
  ).start(wait = true)
}

data class A(val a: String, val aa: Int)
data class B(
  @KompendiumField(name = "AYY", description = "a field")
  val a: A,
  val b: Double,
)

data class C(val c: String)

object KompendiumTOC {
  val testIdGetInfo = MethodInfo("Get Test", "Test for getting", tags = setOf("test", "example", "get"))
  val testSingleRouteInfo = RouteInfo("Test", "Route for test")
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
        notarizedGet(testIdGetInfo) {
          call.respondText("get by id")
        }
      }
      notarizedRoute(path = "/single", info = testSingleRouteInfo) {
        notarizedGet(testSingleGetInfo) {
          call.respondText("get single")
        }
        notarizedPost(testSinglePostInfo) {
          call.respondText("test post")
        }
        notarizedPut<A, B, C>(testSinglePutInfo) {
          call.respondText { "hey" }
        }
      }
    }
    route("/openapi.json") {
      get {
        call.respond(openApiSpec)
      }
    }
  }
}
