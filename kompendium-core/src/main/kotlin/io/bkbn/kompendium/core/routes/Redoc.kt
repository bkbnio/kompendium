package io.bkbn.kompendium.core.routes

import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

/**
 * Provides an out-of-the-box route to view docs using ReDoc
 * @param oas spec to reference
 * @param specUrl url to point ReDoc to the OpenAPI json document
 */
fun Routing.redoc(oas: OpenApiSpec, specUrl: String = "/openapi.json") {
  route("/docs") {
    get {
      call.respondHtml {
        head {
          title {
            +"${oas.info.title}"
          }
          meta {
            charset = "utf-8"
          }
          meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
          }
          link {
            href = "https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700"
            rel = "stylesheet"
          }
          style {
            unsafe {
              raw("body { margin: 0; padding: 0; }")
            }
          }
        }
        body {
          unsafe { +"<redoc spec-url='${specUrl}'></redoc>" }
          script {
            src = "https://cdn.jsdelivr.net/npm/redoc@next/bundles/redoc.standalone.js"
          }
        }
      }
    }
  }
}
