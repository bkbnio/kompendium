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
import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.enrichment.MapEnrichment
import io.bkbn.kompendium.enrichment.NumberEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.StringEnrichment
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
            enrichment = ObjectEnrichment("simple") {
              TestResponse::c {
                StringEnrichment("blah-blah-blah").apply {
                  description = "A simple description"
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

fun Routing.enrichedSimpleRequest() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = ObjectEnrichment("simple") {
              TestSimpleRequest::a {
                StringEnrichment("blah-blah-blah").apply {
                  description = "A simple description"
                }
              }
              TestSimpleRequest::b {
                NumberEnrichment("blah-blah-blah").apply {
                  deprecated = true
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

fun Routing.enrichedNestedCollection() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = ObjectEnrichment("simple") {
              ComplexRequest::tables {
                CollectionEnrichment<NestedComplexItem>("blah-blah").apply {
                  description = "A nested description"
                  itemEnrichment = ObjectEnrichment("nested") {
                    NestedComplexItem::name {
                      StringEnrichment("beleheh").apply {
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

fun Routing.enrichedTopLevelCollection() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType(
            enrichment = CollectionEnrichment<List<TestSimpleRequest>>("blah-blah") {
              itemEnrichment = ObjectEnrichment("simple") {
                TestSimpleRequest::a {
                  StringEnrichment("blah-blah-blah").apply {
                    description = "A simple description"
                  }
                }
                TestSimpleRequest::b {
                  NumberEnrichment("blah-blah-blah").apply {
                    deprecated = true
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
            enrichment = ObjectEnrichment("simple") {
              MultiNestedGenerics<String, ComplexRequest>::content {
                MapEnrichment<String, ComplexRequest>("blah").apply {
                  description = "A nested description"
                  valueEnrichment = ObjectEnrichment("nested") {
                    ComplexRequest::tables {
                      CollectionEnrichment<NestedComplexItem>("blah-blah").apply {
                        description = "A nested description"
                        itemEnrichment = ObjectEnrichment("nested") {
                          NestedComplexItem::name {
                            StringEnrichment("beleheh").apply {
                              description = "A nested description"
                            }
                          }
                        }
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
            enrichment = ObjectEnrichment("generic") {
              GenericObject<TestSimpleRequest>::data {
                ObjectEnrichment("simple") {
                  TestSimpleRequest::a {
                    StringEnrichment("blah-blah-blah").apply {
                      description = "A simple description"
                    }
                  }
                  TestSimpleRequest::b {
                    NumberEnrichment("blah-blah-blah").apply {
                      deprecated = true
                    }
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
