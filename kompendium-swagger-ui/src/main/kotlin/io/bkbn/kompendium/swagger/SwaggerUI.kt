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
import org.webjars.WebJarAssetLocator

@Suppress("unused")
class SwaggerUI(val config: Configuration) {

  class Configuration {
    // The primary Swagger-UI page (will redirect to target page)
    var swaggerUrl: String = "/swagger-ui"
    // The Root path to Swagger resources (in most cases a path to the webjar resources)
    var swaggerBaseUrl: String = "/webjars/swagger-ui"
    // Configuration for SwaggerUI JS initialization
    lateinit var jsConfig: JsConfig
    // Application context path (for example if application have the following path: http://domain.com/app, use: "/app")
    var contextPath: String = ""
  }

  companion object Feature: ApplicationFeature<Application, Configuration, SwaggerUI> {

    private fun Configuration.toInternal(): InternalConfiguration = InternalConfiguration(
      swaggerUrl = URI(swaggerUrl),
      swaggerBaseUrl = URI(swaggerBaseUrl),
      jsConfig = jsConfig,
      contextPath = contextPath
    )

    private data class InternalConfiguration(
      val swaggerUrl: URI,
      val swaggerBaseUrl: URI,
      val jsConfig: JsConfig,
      val contextPath: String
    ) {
      val redirectIndexUrl: String = "${contextPath}${swaggerBaseUrl}/index.html"
    }

    private val locator = WebJarAssetLocator()

    private val installRoutes: Routing.(InternalConfiguration) -> Unit = { config ->

      get(config.swaggerUrl.toString()) {
        call.respondRedirect(config.redirectIndexUrl)
      }

      get("${config.swaggerBaseUrl}/{filename}") {
        call.parameters["filename"]!!.let { filename ->
          when(filename) {
            "swagger-initializer.js" ->
              locator.getSwaggerInitializerContent(jsConfig = config.jsConfig)
            else ->
              locator.getSwaggerResourceContent(path = filename)
          }
        }.let { call.respond(HttpStatusCode.OK, it) }
      }
    }

    override val key: AttributeKey<SwaggerUI> = AttributeKey("SwaggerUI")

    override fun install(pipeline: Application, configure: Configuration.() -> Unit): SwaggerUI {
      pipeline.routing { }.let { routing ->
        val configuration: Configuration = Configuration().apply(configure)
        installRoutes(routing, configuration.toInternal())
        return SwaggerUI(config = configuration)
      }
    }
  }
}
