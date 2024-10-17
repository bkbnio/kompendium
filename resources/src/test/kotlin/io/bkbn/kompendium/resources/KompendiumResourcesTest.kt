package io.bkbn.kompendium.resources

import Listing
import Type
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

class KompendiumResourcesTest : DescribeSpec({
  describe("NotarizedResources Tests") {
    it("Can notarize a simple resource") {
      openApiTestAllSerializers(
        snapshotName = "T0001__simple_resource.json",
        applicationSetup = {
          install(Resources)
          install(NotarizedResources()) {
            resources = mapOf(
              Listing::class to NotarizedResources.ResourceMetadata(
                parameters = listOf(
                  Parameter(
                    name = "name",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.STRING
                  ),
                  Parameter(
                    name = "page",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.INT
                  )
                ),
                get = GetInfo.builder {
                  summary("Resource")
                  description("example resource")
                  response {
                    responseCode(HttpStatusCode.OK)
                    responseType<TestResponse>()
                    description("does great things")
                  }
                }
              ),
            )
          }
        }
      ) {
        get<Listing> { listing ->
          call.respondText("Listing ${listing.name}, page ${listing.page}")
        }
      }
    }
    it("Can notarize nested resources") {
      openApiTestAllSerializers(
        snapshotName = "T0002__nested_resources.json",
        applicationSetup = {
          install(Resources)
          install(NotarizedResources()) {
            resources = mapOf(
              Type.Edit::class to NotarizedResources.ResourceMetadata(
                parameters = listOf(
                  Parameter(
                    name = "name",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.STRING
                  )
                ),
                get = GetInfo.builder {
                  summary("Edit")
                  description("example resource")
                  response {
                    responseCode(HttpStatusCode.OK)
                    responseType<TestResponse>()
                    description("does great things")
                  }
                }
              ),
              Type.Other::class to NotarizedResources.ResourceMetadata(
                parameters = listOf(
                  Parameter(
                    name = "name",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.STRING
                  ),
                  Parameter(
                    name = "page",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.INT
                  )
                ),
                get = GetInfo.builder {
                  summary("Other")
                  description("example resource")
                  response {
                    responseCode(HttpStatusCode.OK)
                    responseType<TestResponse>()
                    description("does great things")
                  }
                }
              ),
            )
          }
        }
      ) {
        get<Type.Edit> { edit ->
          call.respondText("Listing ${edit.parent.name}")
        }
        get<Type.Other> { other ->
          call.respondText("Listing ${other.parent.name}, page ${other.page}")
        }
      }
    }
  }
  describe("NotarizedResource Tests") {
    it("Can notarize resources in route") {
      openApiTestAllSerializers(
        snapshotName = "T0003__resources_in_route.json",
        applicationSetup = {
          install(Resources)
        }
      ) {
        route("/api") {
          typeEditDocumentation()
          get<Type.Edit> { edit ->
            call.respondText("Listing ${edit.parent.name}")
          }
          typeOtherDocumentation()
          get<Type.Other> { other ->
            call.respondText("Listing ${other.parent.name}, page ${other.page}")
          }
        }
      }
    }
  }
})

private fun Route.typeOtherDocumentation() {
  install(NotarizedResource<Type.Other>()) {
    parameters = listOf(
      Parameter(
        name = "name",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      ),
      Parameter(
        name = "page",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.INT
      )
    )
    get = GetInfo.builder {
      summary("Other")
      description("example resource")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<TestResponse>()
        description("does great things")
      }
    }
  }
}

private fun Route.typeEditDocumentation() {
  install(NotarizedResource<Type.Edit>()) {
    parameters = listOf(
      Parameter(
        name = "name",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      )
    )
    get = GetInfo.builder {
      summary("Edit")
      description("example resource")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<TestResponse>()
        description("does great things")
      }
    }
  }
}
