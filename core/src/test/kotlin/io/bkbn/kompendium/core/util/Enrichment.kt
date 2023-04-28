package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.MultiNestedGenerics
import io.bkbn.kompendium.core.fixtures.NestedComplexItem
import io.bkbn.kompendium.core.fixtures.TestCreatedResponse
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.core.fixtures.GenericObject
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.enrichment.TypeEnrichment
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

fun Routing.enrichedSimpleResponse() {
  route("/enriched") {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        response {
          responseType(
            enrichment = TypeEnrichment("simple") {
              TestResponse::c {
                description = "A simple description"
              }
            }
          )
          description("A good response")
          responseCode(HttpStatusCode.Created)
        }
      }
    }
  }
}

fun Routing.enrichedSimpleRequest() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = TypeEnrichment("simple") {
              TestSimpleRequest::a {
                description = "A simple description"
              }
              TestSimpleRequest::b {
                deprecated = true
              }
            }
          )
          description("A test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(TestModules.defaultResponseDescription)
        }
      }
    }
  }
}

fun Routing.enrichedNestedCollection() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = TypeEnrichment("simple") {
              ComplexRequest::tables {
                description = "A nested item"
                typeEnrichment = TypeEnrichment("nested") {
                  NestedComplexItem::name {
                    description = "A nested description"
                  }
                }
              }
            }
          )
          description("A test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(TestModules.defaultResponseDescription)
        }
      }
    }
  }
}

fun Routing.enrichedComplexGenericType() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = TypeEnrichment("simple") {
              MultiNestedGenerics<String, ComplexRequest>::content {
                description = "Getting pretty crazy"
                typeEnrichment = TypeEnrichment("nested") {
                  ComplexRequest::tables {
                    description = "A nested item"
                    typeEnrichment = TypeEnrichment("nested") {
                      NestedComplexItem::name {
                        description = "A nested description"
                      }
                    }
                  }
                }
              }
            }
          )
          description("A test request")
        }
        response {
          responseCode(HttpStatusCode.Created)
          responseType<TestCreatedResponse>()
          description(TestModules.defaultResponseDescription)
        }
      }
    }
  }
}

fun Routing.enrichedGenericResponse() {
  route("/example") {
    install(NotarizedRoute()) {
      get = GetInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        response {
          responseType(
            enrichment = TypeEnrichment("generic") {
              GenericObject<TestSimpleRequest>::data {
                description = "A simple description"
                typeEnrichment = TypeEnrichment("simple") {
                  TestSimpleRequest::a {
                    description = "A simple description"
                  }
                  TestSimpleRequest::b {
                    deprecated = true
                  }
                }
              }
            }
          )
          description("A good response")
          responseCode(HttpStatusCode.Created)
        }
      }
    }
  }
}
