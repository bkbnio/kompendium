package io.bkbn.kompendium.locations

import Listing
import io.bkbn.kompendium.core.fixtures.TestHelpers.openApiTestAllSerializers
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.locations.Locations
import io.ktor.server.locations.get
import io.ktor.server.response.respondText

class KompendiumLocationsTest : DescribeSpec({
  describe("Location Tests") {
    it("Can notarize a simple location") {
      openApiTestAllSerializers(
        snapshotName = "T0001__simple_location.json",
        applicationSetup = {
          install(Locations)
          install(NotarizedLocations()) {
            locations = mapOf(
              Listing::class to NotarizedLocations.LocationMetadata(
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
                  summary("Location")
                  description("example location")
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
    it("Can notarize nested locations") {
      openApiTestAllSerializers(
        snapshotName = "T0002__nested_locations.json",
        applicationSetup = {
          install(Locations)
          install(NotarizedLocations()) {
            locations = mapOf(
              Type.Edit::class to NotarizedLocations.LocationMetadata(
                parameters = listOf(
                  Parameter(
                    name = "name",
                    `in` = Parameter.Location.path,
                    schema = TypeDefinition.STRING
                  )
                ),
                get = GetInfo.builder {
                  summary("Edit")
                  description("example location")
                  response {
                    responseCode(HttpStatusCode.OK)
                    responseType<TestResponse>()
                    description("does great things")
                  }
                }
              ),
              Type.Other::class to NotarizedLocations.LocationMetadata(
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
                  description("example location")
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
})
