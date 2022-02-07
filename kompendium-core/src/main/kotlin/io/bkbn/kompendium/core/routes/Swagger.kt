package io.bkbn.kompendium.core.routes

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe

fun Routing.swagger(pageTitle: String = "Docs", specUrl: String = "/openapi.json") {
  route("/swagger-ui") {
    get {
      call.respondHtml {
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
            href = "https://unpkg.com/swagger-ui-dist@3.12.1/swagger-ui.css"
            rel = "stylesheet"
          }
        }
        body {
          div {
            id = "swagger-ui"
          }
          script {
            src = "https://unpkg.com/swagger-ui-dist@3.12.1/swagger-ui-standalone-preset.js"
          }
          script {
            src = "https://unpkg.com/swagger-ui-dist@3.12.1/swagger-ui-bundle.js"
          }
          unsafe {
            +"""
              <script>

                window.onload = function () {
                  // Build a system
                  const ui = SwaggerUIBundle({
                    url: "$specUrl",
                    dom_id: '#swagger-ui',
                    deepLinking: true,
                    presets: [
                      SwaggerUIBundle.presets.apis,
                      SwaggerUIStandalonePreset
                    ],
                    plugins: [
                      SwaggerUIBundle.plugins.DownloadUrl
                    ],
                    layout: "StandaloneLayout",
                  })
                  window.ui = ui
                }
              </script>
            """.trimIndent()
          }
        }
      }
    }
  }
}
