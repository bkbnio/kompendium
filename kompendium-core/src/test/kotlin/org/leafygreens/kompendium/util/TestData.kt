package org.leafygreens.kompendium.util

import java.io.File
import java.net.URI
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponents
import org.leafygreens.kompendium.models.oas.OpenApiSpecExternalDocumentation
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecOAuthFlow
import org.leafygreens.kompendium.models.oas.OpenApiSpecOAuthFlows
import org.leafygreens.kompendium.models.oas.OpenApiSpecParameter
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaArray
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaRef
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaSecurity
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaString
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.models.oas.OpenApiSpecTag

object TestData {
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  val testSpec = OpenApiSpec(
    info = OpenApiSpecInfo(
      title = "Swagger Petstore",
      description = """
        This is a sample server Petstore server.  You can find out more about Swagger at
        [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).
        For this sample, you can use the api key `special-key` to test the authorization filters.
      """.trimIndent(),
      termsOfService = URI("http://swagger.io/terms/"),
      contact = OpenApiSpecInfoContact(
        name = "Team Swag",
        email = "apiteam@swagger.io"
      ),
      license = OpenApiSpecInfoLicense(
        name = "Apache 2.0",
        url = URI("http://www.apache.org/licenses/LICENSE-2.0.html")
      ),
      version = "1.0.0"
    ),
    externalDocs = OpenApiSpecExternalDocumentation(
      description = "Find out more about Swagger",
      url = URI("http://swagger.io")
    ),
    servers = mutableListOf(
      OpenApiSpecServer(
        url = URI("https://petstore.swagger.io/v2")
      ),
      OpenApiSpecServer(
        url = URI("http://petstore.swagger.io/v2")
      )
    ),
    tags = mutableListOf(
      OpenApiSpecTag(
        name = "pet",
        description = "Everything about your Pets",
        externalDocs = OpenApiSpecExternalDocumentation(
          description = "Find out more",
          url = URI("http://swagger.io")
        )
      ),
      OpenApiSpecTag(
        name = "store",
        description = "Access to Petstore orders"
      ),
      OpenApiSpecTag(
        name = "user",
        description = "Operations about user",
        externalDocs = OpenApiSpecExternalDocumentation(
          description = "Find out more about our store",
          url = URI("http://swagger.io")
        )
      )
    ),
    paths = mutableMapOf(
      "/pet" to OpenApiSpecPathItem(
        put = OpenApiSpecPathItemOperation(
          tags = setOf("pet"),
          summary = "Update an existing pet",
          operationId = "updatePet",
          requestBody = OpenApiSpecRequest(
            description = "Pet object that needs to be added to the store",
            content = mapOf(
              "application/json" to OpenApiSpecMediaType.Explicit(
                schema = OpenApiSpecSchemaRef(`$ref` = "#/components/schemas/Pet")
              ),
              "application/xml" to OpenApiSpecMediaType.Explicit(
                schema = OpenApiSpecSchemaRef(`$ref` = "#/components/schemas/Pet")
              )
            ),
            required = true
          ),
          responses = mapOf(
            400 to OpenApiSpecResponse(
              description = "Invalid ID supplied",
              content = emptyMap()
            ),
            404 to OpenApiSpecResponse(
              description = "Pet not found",
              content = emptyMap()
            ),
            405 to OpenApiSpecResponse(
              description = "Validation exception",
              content = emptyMap()
            )
          ),
          security = listOf(
            mapOf(
              "petstore_auth" to listOf("write:pets", "read:pets")
            )
          ),
          `x-codegen-request-body-name` = "body"
        ),
        post = OpenApiSpecPathItemOperation(
          tags = setOf("pet"),
          summary = "Add a new pet to the store",
          operationId = "addPet",
          requestBody = OpenApiSpecRequest(
            description = "Pet object that needs to be added to the store",
            content = mapOf(
              "application/json" to OpenApiSpecMediaType.Referenced(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              ),
              "application/xml" to OpenApiSpecMediaType.Referenced(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              )
            )
          ),
          responses = mapOf(
            405 to OpenApiSpecResponse(
              description = "Invalid Input",
              content = emptyMap()
            )
          ),
          security = listOf(
            mapOf(
              "petstore_auth" to listOf("write:pets", "read:pets")
            )
          ),
          `x-codegen-request-body-name` = "body"
        )
      ),
      "/pet/findByStatus" to OpenApiSpecPathItem(
        get = OpenApiSpecPathItemOperation(
          tags = setOf("pet"),
          summary = "Find Pets by status",
          description = "Multiple status values can be provided with comma separated strings",
          operationId = "findPetsByStatus",
          parameters = listOf(
            OpenApiSpecParameter(
              name = "status",
              `in` = "query",
              description = "Status values that need to be considered for filter",
              required = true,
              style = "form",
              explode = true,
              schema = OpenApiSpecSchemaArray(
                items = OpenApiSpecSchemaString(
                  default = "available",
                  `enum` = setOf("available", "pending", "sold")
                )
              )
            )
          ),
          responses = mapOf(
            200 to OpenApiSpecResponse(
              description = "successful operation",
              content = mapOf(
                "application/xml" to OpenApiSpecMediaType.Explicit(
                  schema = OpenApiSpecSchemaArray(
                    items = OpenApiSpecSchemaRef("#/components/schemas/Pet")
                  )
                ),
                "application/json" to OpenApiSpecMediaType.Explicit(
                  schema = OpenApiSpecSchemaArray(
                    items = OpenApiSpecSchemaRef("#/components/schemas/Pet")
                  )
                )
              )
            ),
            400 to OpenApiSpecResponse(
              description = "Invalid status value",
              content = mapOf()
            )
          ),
          security = listOf(mapOf(
            "petstore_auth" to listOf("write:pets", "read:pets")
          ))
        )
      )
    ),
    components = OpenApiSpecComponents(
      securitySchemes = mutableMapOf(
        "petstore_auth" to OpenApiSpecSchemaSecurity(
          type = "oauth2",
          flows = OpenApiSpecOAuthFlows(
            implicit = OpenApiSpecOAuthFlow(
              authorizationUrl = URI("http://petstore.swagger.io/oauth/dialog"),
              scopes = mapOf(
                "write:pets" to "modify pets in your account",
                "read:pets" to "read your pets"
              )
            )
          )
        ),
        "api_key" to OpenApiSpecSchemaSecurity(
          type = "apiKey",
          name = "api_key",
          `in` = "header"
        )
      ),
      schemas = mutableMapOf()
    )
  )
}
