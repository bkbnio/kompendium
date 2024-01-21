Kompendium enables users to enrich their payloads with additional metadata
such as field description, deprecation, and more.

Enrichments, unlike annotations, are fully decoupled from the implementation of the class
itself. As such, we can not only enable different metadata on the same class in different
areas of the application, we can also reuse the same metadata in different areas, and even
support enrichment of types that you do not own, or types that are not easily annotated,
such as collections and maps.

A simple enrichment example looks like the following:

```kotlin
post = PostInfo.builder {
  summary(TestModules.defaultPathSummary)
  description(TestModules.defaultPathDescription)
  request {
    requestType(
      enrichment = ObjectEnrichment("simple") {
        TestSimpleRequest::a {
          StringEnrichment(id = "simple-enrichment") {
            description = "A simple description"
          }
        }
        TestSimpleRequest::b {
          NumberEnrichment(id = "blah-blah-blah") {
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
```

For more information on the various enrichment types, please see the following sections.

## Scalar Enrichment

Currently, Kompendium supports enrichment of the following scalar types:

- Boolean
- String
- Number

At the moment, all of these types extend a sealed interface `Enrichment`... as such you cannot provide
enrichments for custom scalars like dates and times. This is a known limitation, and will be addressed
in a future release.

## Object Enrichment

Object enrichment is the most common form of enrichment, and is used to enrich a complex type, and
the fields of a class.

## Collection Enrichment

Collection enrichment is used to enrich a collection type, and the elements of that collection.

```kotlin
post = PostInfo.builder {
  summary(TestModules.defaultPathSummary)
  description(TestModules.defaultPathDescription)
  request {
    requestType(
      enrichment = ObjectEnrichment("simple") {
        ComplexRequest::tables {
          CollectionEnrichment<NestedComplexItem>("blah-blah") {
            description = "A nested description"
            itemEnrichment = ObjectEnrichment("nested") {
              NestedComplexItem::name {
                StringEnrichment("beleheh") {
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
```

## Map Enrichment

Map enrichment is used to enrich a map type, and the keys and values of that map.

```kotlin
get = GetInfo.builder {
  summary(TestModules.defaultPathSummary)
  description(TestModules.defaultPathDescription)
  response {
    responseType<Map<String, TestSimpleRequest>>(
      enrichment = MapEnrichment("blah") {
        description = "A nested description"
        valueEnrichment = ObjectEnrichment("nested") {
          TestSimpleRequest::a {
            StringEnrichment("blah-blah-blah") {
              description = "A simple description"
            }
          }
          TestSimpleRequest::b {
            NumberEnrichment("blah-blah-blah") {
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
```
