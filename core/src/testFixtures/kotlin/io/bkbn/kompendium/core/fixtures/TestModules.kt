package io.bkbn.kompendium.core.fixtures

//import com.fasterxml.jackson.annotation.JsonInclude
//import com.fasterxml.jackson.databind.SerializationFeature
//import io.bkbn.kompendium.core.Kompendium
//import io.bkbn.kompendium.core.fixtures.TestSpecs.defaultSpec
//import io.bkbn.kompendium.core.routes.redoc
//import io.ktor.application.Application
//import io.ktor.application.install
//import io.ktor.features.ContentNegotiation
//import io.ktor.http.ContentType
//import io.ktor.jackson.jackson
//import io.ktor.routing.routing
//
//fun Application.docs() {
//  routing {
//    redoc()
//  }
//}
//
//fun Application.jacksonConfigModule() {
//  install(ContentNegotiation) {
//    jackson(ContentType.Application.Json) {
//      enable(SerializationFeature.INDENT_OUTPUT)
//      setSerializationInclusion(JsonInclude.Include.NON_NULL)
//    }
//  }
//}
//
//fun Application.kompendium(configurer: Kompendium.Configuration.() -> Unit = {}) {
//  install(Kompendium) {
//    spec = defaultSpec()
//    configurer()
//  }
//}
