package io.bkbn.kompendium.core.fixtures

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.fixtures.TestSpecs.defaultSpec
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.json.schema.KotlinXSerializableReader
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.oas.server.Server
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.Routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.reflect.KType
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI

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
  private suspend fun ApplicationTestBuilder.compareOpenAPISpec(snapshotName: String) {
    val response = client.get(OPEN_API_ENDPOINT)
    response shouldHaveStatus HttpStatusCode.OK
    response.bodyAsText() shouldNot beBlank()
    response.bodyAsText() shouldEqualJson getFileSnapshot(snapshotName)
  }

  /**
   * This will take a provided JSON snapshot file, retrieve it from the resource folder,
   * and build a test ktor server to compare the expected output with the output found in the default
   * OpenAPI json endpoint.  By default, this will run the same test with Gson, Kotlinx, and Jackson serializers
   * @param snapshotName The snapshot file to retrieve from the resources folder
   */
  fun openApiTestAllSerializers(
    snapshotName: String,
    customTypes: Map<KType, JsonSchema> = emptyMap(),
    applicationSetup: Application.() -> Unit = { },
    specOverrides: OpenApiSpec.() -> OpenApiSpec = { this },
    routeUnderTest: Routing.() -> Unit
  ) {
    openApiTest(snapshotName, SupportedSerializer.KOTLINX, routeUnderTest, applicationSetup, specOverrides, customTypes)
    openApiTest(snapshotName, SupportedSerializer.JACKSON, routeUnderTest, applicationSetup, specOverrides, customTypes)
    openApiTest(snapshotName, SupportedSerializer.GSON, routeUnderTest, applicationSetup, specOverrides, customTypes)
  }

  private fun openApiTest(
    snapshotName: String,
    serializer: SupportedSerializer,
    routeUnderTest: Routing.() -> Unit,
    applicationSetup: Application.() -> Unit,
    specOverrides: OpenApiSpec.() -> OpenApiSpec,
    typeOverrides: Map<KType, JsonSchema> = emptyMap()
  ) = testApplication {
    install(NotarizedApplication()) {
      customTypes = typeOverrides
      spec = defaultSpec().specOverrides()
      serializerReader = when (serializer) {
        SupportedSerializer.KOTLINX -> KotlinXSerializableReader()
        SupportedSerializer.GSON -> GsonSerializableReader()
        SupportedSerializer.JACKSON -> JacksonSerializableReader()
      }
    }
    install(ContentNegotiation) {
      when (serializer) {
        SupportedSerializer.KOTLINX -> json(Json {
          encodeDefaults = true
          explicitNulls = false
          serializersModule = KompendiumSerializersModule.module
        })

        SupportedSerializer.GSON -> gson()
        SupportedSerializer.JACKSON -> jackson(ContentType.Application.Json) {
          enable(SerializationFeature.INDENT_OUTPUT)
          setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
      }
    }
    application(applicationSetup)
    routing {
      redoc()
      routeUnderTest()
    }
    compareOpenAPISpec(snapshotName)
  }
}
