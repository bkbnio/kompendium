package io.bkbn.kompendium.swagger

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import java.net.URI

@Suppress("unused")
class SwaggerUI(val config: Configuration) {

  data class SwaggerUIConfig(
    val baseUrl: String,
    val version: String,
    val specs: Map<String, URI>,
    val contextPath: String = "",
    val jsInit: () -> String? = { null }
  ) {
    private val webJarsUrl = "/webjars"
    val swaggerUiUrl = "$webJarsUrl/swagger-ui"
    private val swaggerIndexUrl = "$swaggerUiUrl/index.html"
    val redirectIndexUrl: String = "${contextPath}${swaggerIndexUrl}"
  }

  class Configuration {
    lateinit var config: SwaggerUIConfig
  }

  companion object Feature: ApplicationFeature<Application, Configuration, SwaggerUI> {
    private val installRoutes: Routing.(SwaggerUIConfig) -> Unit = { config ->

      get(config.baseUrl) {
        call.respondRedirect(config.redirectIndexUrl)
      }

      get("${config.swaggerUiUrl}/{filename}") {
        call.parameters["filename"]!!.let { filename ->
          when(filename) {
            "index.html" ->
              getSwaggerIndexContent(
                swaggerVersion = config.version,
                specs = config.specs,
                jsInit = config.jsInit,
              )
            else ->
              getSwaggerResourceContent(swaggerVersion = config.version, filePath = filename)
          }
        }.let { call.respond(HttpStatusCode.OK, it) }
      }
    }

    override val key: AttributeKey<SwaggerUI> = AttributeKey("SwaggerUI")

    override fun install(pipeline: Application, configure: Configuration.() -> Unit): SwaggerUI {
      pipeline.routing { }.let { routing ->
        val configuration = Configuration().apply(configure)
        installRoutes(routing, configuration.config)
        return SwaggerUI(config = configuration)
      }
    }
  }
}
