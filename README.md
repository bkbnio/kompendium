# Kompendium

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a9bfd6c77d22497b907b3221849a3ba9)](https://www.codacy.com/gh/bkbnio/kompendium/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bkbnio/kompendium&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/a9bfd6c77d22497b907b3221849a3ba9)](https://www.codacy.com/gh/bkbnio/kompendium/dashboard?utm_source=github.com&utm_medium=referral&utm_content=bkbnio/kompendium&utm_campaign=Badge_Coverage)
[![version](https://img.shields.io/maven-central/v/io.bkbn/kompendium-core?style=flat-square)](https://search.maven.org/search?q=io.bkbn%20kompendium)

## What is Kompendium

Kompendium is intended to be a minimally invasive OpenApi Specification generator for [Ktor](https://ktor.io). 
Minimally invasive meaning that users will use only Ktor native functions when implementing their API, and will 
supplement with Kompendium code in order to generate the appropriate spec. 

## How to install

Kompendium publishes all releases to Maven Central.  As such, using the stable version of `Kompendium` is as simple 
as declaring it as an implementation dependency in your `build.gradle.kts`

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.bkbn:kompendium-core:1.8.1")
  implementation("io.bkbn:kompendium-auth:1.8.1")
  implementation("io.bkbn:kompendium-swagger-ui:1.8.1")
  
  // Other dependencies...
}
```

The last two dependencies are optional.

If you want to get a little spicy 🤠 every merge of Kompendium is published to the GitHub package registry.  Pulling 
from GitHub is slightly more involved, but such is the price you pay for bleeding edge fake data generation.  

```kotlin
// 1 Setup a helper function to import any Github Repository Package
// This step is optional but I have a bunch of stuff stored on github so I find it useful 😄
fun RepositoryHandler.github(packageUrl: String) = maven { 
    name = "GithubPackages"
    url = uri(packageUrl)
    credentials {
      username = java.lang.System.getenv("GITHUB_USER")
      password = java.lang.System.getenv("GITHUB_TOKEN")
    } 
}

// 2 Add the repo in question (in this case Kompendium)
repositories {
    github("https://maven.pkg.github.com/bkbnio/kompendium")
}

// 3 Add the package like any normal dependency
dependencies { 
    implementation("io.bkbn:kompendium-core:1.8.1")
}

```

## Local Development

Kompendium should run locally right out of the box, no configuration necessary (assuming you have JDK 1.8+ installed).  New features can be built locally and published to your local maven repository with the `./gradlew publishToMavenLocal` command! 

## In depth

### Notarized Routes 

Kompendium introduces the concept of `notarized` HTTP methods.  That is, for all your `GET`, `POST`, `PUT`, and `DELETE`
operations, there is a corresponding `notarized` method.  These operations are strongly typed, and use reification for 
a lot of the class based reflection that powers Kompendium.  Generally speaking the three types that a `notarized` method
will consume are

- `TParam`: Used to notarize expected request parameters
- `TReq`: Used to build the object schema for a request body
- `TResp`: Used to build the object schema for a response body

`GET` and `DELETE` take `TParam` and `TResp` while `PUT` and `POST` take all three.


In addition to standard HTTP Methods, Kompendium also introduced the concept of `notarizedExceptions`.  Using the `StatusPage`
extension, users can notarize all handled exceptions, along with their respective HTTP codes and response types. 
Exceptions that have been `notarized` require two types as supplemental information

- `TErr`: Used to notarize the exception being handled by this use case.  Used for matching responses at the route level.
- `TResp`: Same as above, this dictates the expected return type of the error response.  

In keeping with minimal invasion, these extension methods all consume the same code block as a standard Ktor route method,
meaning that swapping in a default Ktor route and a Kompendium `notarized` route is as simple as a single method change.

### Supplemental Annotations

In general, Kompendium tries to limit the number of annotations that developers need to use in order to get an app 
integrated.   

Currently, the annotations used by Kompendium are as follows 

- `KompendiumField`
- `KompendiumParam`

The intended purpose of `KompendiumField` is to offer field level overrides such as naming conventions (ie snake instead of camel).

The purpose of `KompendiumParam` is to provide supplemental information needed to properly assign the type of parameter 
(cookie, header, query, path) as well as other parameter-level metadata. 

### Undeclared Field

There is also a final `UndeclaredField` annotation.  This should be used only in an absolutely emergency.  This annotation
will allow you to inject a _single_ undeclared field that will be included as part of the schema. 

Due to limitations in using repeated annotations, this can only be used once per class

This is a complete hack, and is included for odd scenarios like kotlinx serialization polymorphic adapters that expect a 
`type` field in order to perform their analysis.  

Use this _only_ when **all** else fails

### Polymorphism

Speaking of polymorphism... out of the box, Kompendium has support for sealed classes and interfaces. At runtime, it will build a mapping of all available sub-classes
and build a spec that takes `anyOf` the implementations.  This is currently a weak point of the entire library, and 
suggestions on better implementations are welcome 🤠

### Serialization

Under the hood, Kompendium uses Jackson to serialize the final api spec.  However, this implementation detail 
does not leak to the actual API, meaning that users are free to choose the serialization library of their choice.  

Added the possibility to add your own ObjectMapper for Jackson. 

Added a default parameter with the following configuration:

```kotlin
ObjectMapper()
  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
  .enable(SerializationFeature.INDENT_OUTPUT)
```

If you want to change this default configuration and use your own ObjectMapper you only need to pass it as a second argument to the openApi module:

```kotlin
routing {
  openApi(oas, objectMapper)
  route("/potato/spud") {
    notarizedGet(simpleGetInfo) {
      call.respond(HttpStatusCode.OK)
    }
  }
}
```

### Route Handling

> ⚠️ Warning: Custom route handling is almost definitely an indication that either a new selector should be added to kompendium-core or that kompendium is in need of another module to handle a new ktor companion module.  If you have encountered a route selector that is not already handled, please consider opening an [issue](https://github.com/bkbnio/kompendium/issues/new)

Kompendium does its best to handle all Ktor routes out of the gate.  However, in keeping with the modular approach of 
Ktor and Kompendium, this is not always possible. 

Should you need to, custom route handlers can be registered via the 
`Kompendium.addCustomRouteHandler` function.  

The handler signature is as follows

```kotlin
fun <T : RouteSelector> addCustomRouteHandler(
  selector: KClass<T>,
  handler: PathCalculator.(Route, String) -> String
)
```

This function takes a selector, which _must_ be a KClass of the Ktor `RouteSelector` type.  The handler is a function
that extends the Kompendium `PathCalculator`.  This is necessary because it gives you access to `PathCalculator.calculate`, 
which you are going to want in order to recursively calculate the remainder of the route :)

Its parameters are the `Route` itself, along with the "tail" of the Path (the path that has been calculated thus far).

Working examples `init` blocks of the `PathCalculator` and `KompendiumAuth` object.

## Examples

The full source code can be found in the `kompendium-playground` module. Here is a simple get endpoint example 

```kotlin
// Minimal API Example
fun Application.mainModule() {
  install(StatusPages) {
    notarizedException<Exception, ExceptionResponse>(
      info = ResponseInfo(
        KompendiumHttpCodes.BAD_REQUEST,
        "Bad Things Happened"
      )
    ) {
      call.respond(HttpStatusCode.BadRequest, ExceptionResponse("Why you do dis?"))
    }
  }
  routing {
    openApi(oas)
    redoc(oas)
    swaggerUI()
    route("/potato/spud") {
      notarizedGet(simpleGetInfo) {
        call.respond(HttpStatusCode.OK)
      }
    }
  }
}

val simpleGetInfo = GetInfo<Unit, ExampleResponse>(
  summary = "Example Parameters",
  description = "A test for setting parameter examples",
  responseInfo = ResponseInfo(
    status = 200,
    description = "nice",
    examples = mapOf("test" to ExampleResponse(c = "spud"))
  ),
  canThrow = setOf(Exception::class)
)
```

### Kompendium Auth and security schemes

There is a separate library to handle security schemes: `kompendium-auth`. 
This needs to be added to your project as dependency.

At the moment, the basic and jwt authentication is only supported.

A minimal example would be:
```kotlin
install(Authentication) {
  notarizedBasic("basic") {
    realm = "Ktor realm 1"
    // configure basic authentication provider..
  }
  notarizedJwt("jwt") {
    realm = "Ktor realm 2"
    // configure jwt authentication provider...
  }
}
routing {
  authenticate("basic") {
    route("/basic_auth") {
      notarizedGet(basicAuthGetInfo) {
        call.respondText { "basic auth" }
      }
    }
  }
  authenticate("jwt") {
    route("/jwt") {
      notarizedGet(jwtAuthGetInfo) {
        call.respondText { "jwt" }
      }
    }
  }
}

val basicAuthGetInfo = MethodInfo<Unit, ExampleResponse>(
  summary = "Another get test", 
  description = "testing more", 
  responseInfo = testGetResponse, 
  securitySchemes = setOf("basic")
)
val jwtAuthGetInfo = basicAuthGetInfo.copy(securitySchemes = setOf("jwt"))
```

### Enabling Swagger ui
To enable Swagger UI, `kompendium-swagger-ui` needs to be added.
This will also add the [ktor webjars feature](https://ktor.io/docs/webjars.html) to your classpath as it is required for swagger ui.
Minimal Example:
```kotlin
  install(Webjars)
  routing {
    openApi(oas)
    swaggerUI()
  }
```

### Enabling ReDoc
Unlike swagger, redoc is provided (perhaps confusingly, in the `core` module).  This means out of the box with `kompendium-core`, you can add 
[ReDoc](https://github.com/Redocly/redoc) as follows

```kotlin
routing {
  openApi(oas)
  redoc(oas)
}
```

## Custom Type Overrides

Kompendium does its best to analyze types and to generate an OpenAPI format accordingly. However, there are certain 
classes that just don't play nice with the standard reflection analysis that Kompendium performs.
Should you encounter a data type that Kompendium cannot comprehend, you will need to 
add it explicitly.  For example, adding the Joda Time `DateTime` object would be as simple as the following 

```kotlin
  Kompendium.addCustomTypeSchema(DateTime::class, FormatSchema("date-time", "string"))
```

Since `Kompendium` is an object, this needs to be declared once, ahead of the actual API instantiation.  This way, this 
type override can be cached ahead of reflection.  Kompendium will then match all instances of this type and return the 
specified schema.  

So how do you know a type can and cannot be inferred?  The safe bet is that it can be.  So go ahead and give it a shot. 
However, in the very odd scenario (almost always having to do with date/time libraries 😤) where it can't, you can rest
safely knowing that you have the option to inject a custom override should you need to.

## Limitations

### Kompendium as a singleton

Currently, Kompendium exists as a Kotlin object.  This comes with a couple perks, but a couple downsides.  Primarily,
it offers a seriously clean UX where the implementer doesn't need to worry about what instance to send data to. The main
drawback, however, is that you are limited to a single API per classpath.  

If this is a blocker, please open a GitHub issue, and we can start to think out solutions! 

## Future Work
Work on V1 of Kompendium has come to a close.  This, however, does not mean it has achieved complete
parity with the OpenAPI feature spec, nor does it have all-of-the nice to have features that a truly next-gen API spec 
should have.  There are several outstanding features that have been added to the
[V2 Milestone](https://github.com/bkbnio/kompendium/milestone/2).  Among others, this includes 

- AsyncAPI Integration
- Field Validation

If you have a feature that you would like to see implemented that is not on this list, or discover a 🐞, please open 
an issue [here](https://github.com/bkbnio/kompendium/issues/new)
