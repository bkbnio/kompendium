package io.bkbn.kompendium.locations.util

import io.bkbn.kompendium.locations.NotarizedLocation.notarizedDelete
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedGet
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPost
import io.bkbn.kompendium.locations.NotarizedLocation.notarizedPut
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.locations.Locations
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.locationsConfig() {
  install(Locations)
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
