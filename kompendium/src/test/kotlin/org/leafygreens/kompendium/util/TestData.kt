package org.leafygreens.kompendium.util

import java.io.File
import java.net.URI
import org.leafygreens.kompendium.models.OpenApiSpec
import org.leafygreens.kompendium.models.OpenApiSpecExternalDocumentation
import org.leafygreens.kompendium.models.OpenApiSpecInfo
import org.leafygreens.kompendium.models.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.OpenApiSpecRequest
import org.leafygreens.kompendium.models.OpenApiSpecResponse
import org.leafygreens.kompendium.models.OpenApiSpecServer
import org.leafygreens.kompendium.models.OpenApiSpecTag

object TestData {
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  val testSpec = OpenApiSpec(
    info = OpenApiSpecInfo(
      title = "Swagger Petstore",
      description = "This is a sample server Petstore server.  You can find out more about     Swagger at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).      For this sample, you can use the api key `special-key` to test the authorization     filters.",
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
    servers = listOf(
      OpenApiSpecServer(
        url = URI("https://petstore.swagger.io/v2")
      ),
      OpenApiSpecServer(
        url = URI("http://petstore.swagger.io/v2")
      )
    ),
    tags = listOf(
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
    paths = mapOf(
      "/pet" to OpenApiSpecPathItem(
        put = OpenApiSpecPathItemOperation(
          tags = setOf("pet"),
          summary = "Update an existing pet",
          operationId = "updatePet",
          requestBody = OpenApiSpecRequest(
            description = "Pet object that needs to be added to the store",
            content = mapOf(
              "application/json" to OpenApiSpecMediaType(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              ),
              "application/xml" to OpenApiSpecMediaType(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              )
            ),
            required = true
          ),
          responses = mapOf(
            "400" to OpenApiSpecResponse(
              description = "Invalid ID supplied",
              content = emptyMap()
            ),
            "404" to OpenApiSpecResponse(
              description = "Pet not found",
              content = emptyMap()
            ),
            "405" to OpenApiSpecResponse(
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
              "application/json" to OpenApiSpecMediaType(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              ),
              "application/xml" to OpenApiSpecMediaType(
                schema = OpenApiSpecReferenceObject(`$ref` = "#/components/schemas/Pet")
              )
            )
          ),
          responses = mapOf(
            "405" to OpenApiSpecResponse(
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
      )
    )
  )
}
