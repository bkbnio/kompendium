package io.bkbn.kompendium.core.fixtures

import io.bkbn.kompendium.core.fixtures.TestSpecs.defaultSpec
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.json.schema.KotlinXSchemaConfigurator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ServerConfigBuilder
import io.ktor.server.engine.ApplicationEnvironmentBuilder
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.server.routing.Route
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KType

object TestHelpers {
  private const val OPEN_API_ENDPOINT = "/openapi.json"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  /**
   * Performs the baseline expected tests on an OpenAPI result.  Confirms that the endpoint
   * exists as expected, and that the content matches the expected blob found in the specified file
   * @param snapshotName The snapshot file to retrieve from the resources folder
   */
  private suspend fun ApplicationTestBuilder.compareOpenAPISpec(rootPath: String, snapshotName: String) {
    val response = client.get("$rootPath$OPEN_API_ENDPOINT")
    response shouldHaveStatus HttpStatusCode.OK
    response.bodyAsText() shouldNot beBlank()
    response.bodyAsText() shouldEqualJson getFileSnapshot(snapshotName)
  }

  /**
   * This will take a provided JSON snapshot file, retrieve it from the resource folder,
   * and build a test ktor server to compare the expected output with the output found in the default
   * OpenAPI json endpoint.
   * @param snapshotName The snapshot file to retrieve from the resources folder
   */
  fun openApiTestAllSerializers(
    snapshotName: String,
    customTypes: Map<KType, JsonSchema> = emptyMap(),
    applicationSetup: Application.() -> Unit = { },
    specOverrides: OpenApiSpec.() -> OpenApiSpec = { this },
    applicationEnvironmentBuilder: ApplicationEnvironmentBuilder.() -> Unit = {},
    notarizedApplicationConfigOverrides: NotarizedApplication.Config.() -> Unit = {},
    contentNegotiation: ContentNegotiationConfig.() -> Unit = {
      json(Json {
        encodeDefaults = true
        explicitNulls = false
        serializersModule = KompendiumSerializersModule.module
      })
    },

    serverConfigSetup: ServerConfigBuilder.() -> Unit = { },
    routeUnderTest: Route.() -> Unit
  ) {
    openApiTest(
      snapshotName,
      routeUnderTest,
      applicationSetup,
      specOverrides,
      customTypes,
      notarizedApplicationConfigOverrides,
      contentNegotiation,
      applicationEnvironmentBuilder,
      serverConfigSetup
    )
  }

  private fun openApiTest(
    snapshotName: String,
    routeUnderTest: Route.() -> Unit,
    applicationSetup: Application.() -> Unit,
    specOverrides: OpenApiSpec.() -> OpenApiSpec,
    typeOverrides: Map<KType, JsonSchema> = emptyMap(),
    notarizedApplicationConfigOverrides: NotarizedApplication.Config.() -> Unit,
    contentNegotiation: ContentNegotiationConfig.() -> Unit,
    applicationBuilder: ApplicationEnvironmentBuilder.() -> Unit,
    serverConfigSetup: ServerConfigBuilder.() -> Unit
  ) = testApplication {
    environment(applicationBuilder)
    install(NotarizedApplication()) {
      customTypes = typeOverrides
      spec = { specOverrides(defaultSpec()) }
      schemaConfigurator = KotlinXSchemaConfigurator()
      notarizedApplicationConfigOverrides()
    }
    install(ContentNegotiation) {
      contentNegotiation()
    }
    application(applicationSetup)
    serverConfig(serverConfigSetup)
    routing {
      swagger()
      redoc()
      routeUnderTest()
    }

    val root = ServerConfigBuilder(ApplicationEnvironmentBuilder().apply(applicationBuilder).build()).apply(serverConfigSetup).rootPath
    compareOpenAPISpec(root, snapshotName)
  }
}
