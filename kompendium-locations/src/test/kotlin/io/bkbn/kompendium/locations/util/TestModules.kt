package io.bkbn.kompendium.locations.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.routes.openApi
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedDelete
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedGet
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPost
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPut
import io.bkbn.kompendium.locations.util.TestData.oas
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.configModule() {
  install(ContentNegotiation) {
    jackson(ContentType.Application.Json) {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
  install(Locations)
}

fun Application.docs() {
  routing {
    openApi(oas())
    redoc(oas())
  }
}

fun Application.notarizedGetSimpleLocation() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetSimpleLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedGetNestedLocation() {
  routing {
    route("/test") {
      notarizedGet(TestResponseInfo.testGetNestedLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedPostSimpleLocation() {
  routing {
    route("/test") {
      notarizedPost(TestResponseInfo.testPostSimpleLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedPostNestedLocation() {
  routing {
    route("/test") {
      notarizedPost(TestResponseInfo.testPostNestedLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedPutSimpleLocation() {
  routing {
    route("/test") {
      notarizedPut(TestResponseInfo.testPutSimpleLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedPutNestedLocation() {
  routing {
    route("/test") {
      notarizedPut(TestResponseInfo.testPutNestedLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedDeleteSimpleLocation() {
  routing {
    route("/test") {
      notarizedDelete(TestResponseInfo.testDeleteSimpleLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}

fun Application.notarizedDeleteNestedLocation() {
  routing {
    route("/test") {
      notarizedDelete(TestResponseInfo.testDeleteNestedLocation) {
        call.respondText { "hey dude ‼️ congratz on the get request" }
      }
    }
  }
}
