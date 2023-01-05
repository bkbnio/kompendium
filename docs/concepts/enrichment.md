Kompendium allows users to enrich their data types with additional information. This can be done by defining a
`TypeEnrichment` object and passing it to the `enrich` function on the `NotarizedRoute` builder. Enrichments
can be added to any request or response.

```kotlin
data class SimpleData(val a: String, val b: Int? = null)

val myEnrichment = TypeEnrichment<SimpleData>(id = "simple-enrichment") {
  SimpleData::a {
    description = "This will update the field description"
  }
  SimpleData::b {
    // Will indicate in the UI that the field will be removed soon
    deprecated = true
  }
}

// In your route documentation
fun Routing.enrichedSimpleRequest() {
  route("/example") {
    install(NotarizedRoute()) {
      parameters = TestModules.defaultParams
      post = PostInfo.builder {
        summary(TestModules.defaultPathSummary)
        description(TestModules.defaultPathDescription)
        request {
          requestType<SimpleData>(enrichment = myEnrichment) // Simply attach the enrichment to the request
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
```

{% hint style="warning" %}
An enrichment must provide an `id` field that is unique to the data class that is being enriched. This is because
under the hood, Kompendium appends this id to the data class identifier in order to support multiple different
enrichments
on the same data class.

If you provide duplicate ids, all but the first enrichment will be ignored, as Kompendium will view that as a cache hit,
and skip analyzing the new enrichment.
{% endhint %}

### Nested Enrichments

Enrichments are portable and composable, meaning that we can take an enrichment for a child data class
and apply it inside a parent data class using the `typeEnrichment` property.

```kotlin
data class ParentData(val a: String, val b: ChildData)
data class ChildData(val c: String, val d: Int? = null)

val childEnrichment = TypeEnrichment<ChildData>(id = "child-enrichment") {
  ChildData::c {
    description = "This will update the field description of field c on child data"
  }
  ChildData::d {
    description = "This will update the field description of field d on child data"
  }
}

val parentEnrichment = TypeEnrichment<ParentData>(id = "parent-enrichment") {
  ParentData::a {
    description = "This will update the field description"
  }
  ParentData::b {
    description = "This will update the field description of field b on parent data"
    typeEnrichment = childEnrichment // Will apply the child enrichment to the internals of field b
  }
}
```

## Available Enrichments

All enrichments support the following properties:

- description -> Provides a reader friendly description of the field in the object
- deprecated -> Indicates that the field is deprecated and should not be used

### String

- minLength -> The minimum length of the string
- maxLength -> The maximum length of the string
- pattern -> A regex pattern that the string must match
- contentEncoding -> The encoding of the string
- contentMediaType -> The media type of the string

### Numbers

- minimum -> The minimum value of the number
- maximum -> The maximum value of the number
- exclusiveMinimum -> Indicates that the minimum value is exclusive
- exclusiveMaximum -> Indicates that the maximum value is exclusive
- multipleOf -> Indicates that the number must be a multiple of the provided value

### Arrays

- minItems -> The minimum number of items in the array
- maxItems -> The maximum number of items in the array
- uniqueItems -> Indicates that the array must contain unique items
