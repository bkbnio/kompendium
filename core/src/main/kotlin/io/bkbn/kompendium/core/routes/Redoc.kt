package io.bkbn.kompendium.core.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

/**
 * Provides an out-of-the-box route to view docs using ReDoc on the specified [path].
 * @param pageTitle Webpage title you wish to be displayed on your docs
 * @param path path to docs resource
 * @param specUrl url to point ReDoc to the OpenAPI json document
 */
fun Route.redoc(pageTitle: String = "Docs", path: String = "/docs", specUrl: String = "/openapi.json") {
  route(path) {
    get {
      call.respondHtml(HttpStatusCode.OK) {
        head {
          title {
            +pageTitle
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
          unsafe { +"<redoc spec-url='$specUrl'></redoc>" }
          script {
            src = "https://cdn.jsdelivr.net/npm/redoc@next/bundles/redoc.standalone.js"
          }
        }
      }
    }
  }
}
