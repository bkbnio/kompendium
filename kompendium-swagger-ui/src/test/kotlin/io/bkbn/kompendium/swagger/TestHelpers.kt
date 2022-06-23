package io.bkbn.kompendium.swagger

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.fixtures.kompendium
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withApplication
import java.net.URI

object TestHelpers {

    const val TEST_SWAGGER_UI_ROOT = "/swagger-ui"
    const val TEST_SWAGGER_UI_INDEX = "/webjars/swagger-ui/index.html"
    const val TEST_SWAGGER_UI_INIT_JS = "/webjars/swagger-ui/swagger-initializer.js"

    // The same config the same as in Playground
    private val basicSwaggerUiConfig: SwaggerUI.Configuration.() -> Unit = {
        swaggerUrl = "/swagger-ui"
        jsConfig = JsConfig(
            specs = mapOf(
                "My API v1" to URI("/openapi.json"),
                "My API v2" to URI("/openapi.json")
            ),
            jsInit = {
                """
  window.ui.initOAuth({
      clientId: 'CLIENT_ID',
      clientSecret: 'CLIENT_SECRET',
      realm: 'MY REALM',
      appName: 'TEST APP',
      useBasicAuthenticationWithAccessCodeGrant: true
  });
      """
            }
        )
    }

    fun TestApplicationEngine.compareRedirect(resourceName: String, targetResource: String) {
        handleRequest(HttpMethod.Get, resourceName).apply {
            response shouldHaveStatus HttpStatusCode.Found
            response.headers[HttpHeaders.Location] shouldBe targetResource
        }
    }

    fun TestApplicationEngine.compareResource(resourceName: String, contentType: ContentType, mustContain: List<String>) {
        handleRequest(HttpMethod.Get, resourceName).apply {
            response shouldHaveStatus HttpStatusCode.OK
            response.content shouldNotBe null
            response shouldHaveContentTypeMatching contentType
            mustContain.forEach {
                response.content!! shouldContain it
            }
        }
    }

    fun <R> withSwaggerApplication(
        moduleFunction: Application.() -> Unit = {},
        kompendiumConfigurer: Kompendium.Configuration.() -> Unit = {},
        swaggerUIConfigurer: SwaggerUI.Configuration.() -> Unit = basicSwaggerUiConfig,
        test: TestApplicationEngine.() -> R
    ) {
        withApplication(createTestEnvironment()) {
            moduleFunction(application.apply {
                kompendium(kompendiumConfigurer)
                install(SwaggerUI, swaggerUIConfigurer)
            })
            test()
        }
    }

    private infix fun TestApplicationResponse.shouldHaveContentTypeMatching(contentType: ContentType) = this should haveContentTypeMatching(contentType)

    private fun haveContentTypeMatching(contentType: ContentType) = object : Matcher<TestApplicationResponse> {
      override fun test(value: TestApplicationResponse): MatcherResult {
        return MatcherResult(
          value.contentType().match(contentType),
          { "Response should have ContentType $contentType= but was ${value.contentType()}" },
          { "Response should not have ContentType $contentType" }
        )
      }
    }
}
