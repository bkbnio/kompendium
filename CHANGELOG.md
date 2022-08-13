# Changelog

## Unreleased

### Added

### Changed
- Can now put redoc route behind authentication

### Remove

---

## Released

## [2.3.5] - June 7th, 2022
### Added

### Changed
- Fix serialization for api key location in api key auth configuration

### Remove

## [2.3.4] - April 7th, 2022
### Changed
- Put request body info now nullable

## [2.3.3] - April 1st, 2022
### Added
- Added tests for Swagger UI module that verify that plugin generates correct responses for Swagger UI WEB resources (tests should detect future incompatible changes in new versions of `org.webjars.swagger-ui`)

### Changed
- Fixed broken Swagger UI plugin (`org.webjars.swagger-ui` WEB resources structure changed in version 4.9.X). Issue: https://github.com/bkbnio/kompendium/issues/236


## [2.3.2] - March 30th, 2022
### Changed
- Fixed bug where nullable enum fields caused runtime exceptions

## [2.3.1] - March 5th, 2022
### Changed
- Can now apply `@FreeFormObject` to top level types

## [2.3.0] - March 1st, 2022
### Added
- Brand new SwaggerUI support as a KTor plugin with WebJar under the hood and flexible configuration

### Changed
- Playground example `SwaggerPlaygound` now demonstrates new SwaggerUI KTor plugin usage (including OAuth security)

### Remove
- Deprecated Swagger Webjar approach was removed from codebase

## [2.2.1] - February 26th, 2022
- Fix to support sealed class typed Maps

## [2.2.0] - February 25th, 2022
### Changed
- Fixed support Location classes located in other non-location classes
- Fixed formatting of a custom `SimpleSchema`
- Multipart form-data multiple file request support

## [2.1.1] - February 19th, 2022
### Changed
- Fixed sealed typed collections schema generation
- Nullability no longer breaks object schema comparison

## [2.1.0] - February 18th, 2022
### Added
- Ability to override serializer via custom route
### Changed
- All complex types are now represented by reference schemas
- Deprecated `@Referenced` since all complex types now create references

## [2.0.4] - February 10th, 2022
### Added
- Custom Type example to playground

### Changed
- Cleaned up and broke out handlers into separate classes
- Serializer cleanup
- Tests now run against Jackson, Gson and kotlinx on every run
- Swagger UI bumped from v3 to v4

## [2.0.3] - February 7th, 2022
### Changed
- Fixed swagger documentation bug
- Deprecated Swagger Webjar approach

## [2.0.2] - February 4th, 2022
### Added
- `@Referenced` annotation enabling support for recursive models

## [2.0.1] - January 23rd, 2022

### Change
- Fix bug in documentation publishing pipeline

## [2.0.0] - January 23rd, 2022

Major Release 🎉 As we head towards the Ktor 2 release, this library will be kept compatible with Ktor 1. A future
Kompendium 2 repository will be created soon, porting much of the changes you see here, with some awesome Ktor 2 twists
😉

### Added

- Support for HTTP Patch, Head, and Options methods
- Support for including parameter examples via `MethodInfo`
- Dokka Pipeline Generation
- GitHub Pages integration
- Sourdough Gradle updates
- Support for OAuth authentication
- Gradle Toolchain feature to ensure match between local JDK and compile target
- Dokka integration
- Post-processing callback hook
- `description` key to KompendiumField
- Set of base constraints for simple and formatted types
- Ability to document expected unstructured data

### Changed

- Kompendium now leverages the chosen API serializer. Supports Jackson, Gson and Kotlinx Serialization
- Fixed bug where overridden field names were not reflected in serialized object and required array
- Fixed bug where Ktor Location parents were not being scanned for parameters
- `$ref` types are no longer generated, instead all objects are defined explicitly
- All OpenAPI domain models moved to a separate module `kompendium-oas`
- Moved all files in `kompendium-core` into `io.bkbn.kompendium.core` package from `io.bkbn.kompendium`
- Gradle bumped to 7.3.2
- Gradle build logic offloaded to Sourdough Plugin
- Minimum supported Java version is now 11
- Bumped Kotlin to 1.6
- Annotations now live in a separate module.  (Should not impact end users as module is imported as api dependency by
  core).
- Kotest as the testing framework of choice
- Path calculation removed in favor of built-in route toString
- Ktor to 1.6.7
- Completely reworked authentication and exceptions
- MethodInfo now exists in a separate package as a sealed interface, each implementation also has its own file
- Kompendium is now a Ktor Plugin!
- GitHub Actions now leverage Gradle Wrapper
- Dropped Codacy support b/c codacy kinda sucks
- Fixed bug where KompendiumField was being completely ignored
- Redid playground to serve as a showcase for various functionality
- README updates
- Refactored `handleComplexType` 🎉
- Enabled field descriptions
- Dropped Version Catalog
- Responses are now a map of _actual_ responses rather than generic payloads
- Fixed bug where params with default values were listed as required
- Made empty put/post request info opt-in rather than default
- Fields are now marked as required when there is no default, and they are non-nullable
- `KompendiumField` and 'KompendiumParam' renamed to `Field` and `Param` respectively
- Description dropped from `Param`
- Dropped unnecessary parameter content scanning method
- Fixed bug causing all request bodies to be marked as optional
- Dropped ASDF tool manifest

## [1.11.1] - November 25th, 2021

### Added

- Documentation showing how to add header names using Kotlin backtick convention

## [1.11.0] - November 25th, 2021

### Added

- Support for Ktor Location Plugin

## [1.10.0] - November 25th, 2021

### Changed

- Added `operationId` parameter to `MethodInfo`

## [1.9.2] - October 24th, 2021

### Changed

- Jackson ObjectMapper passed by parameter to openapi module
- Added serializable annotation to ExceptionResponse

## [1.9.1] - October 17th, 2021

### Changed

- Code Coverage removed from PR checks due to limitations with GitHub workflows
- Minor linting fixes
- Detekt now builds off of default config

## [1.9.0] - october 15th, 2021

### Added

- ByteArray added to the set of default types

## [1.8.1] - October 4th, 2021

### Added

- Codacy integration

## [1.8.0] - October 4th, 2021

### Changed

- Path calculation revamped to allow for simpler selector injection
- Kotlin version bumped to 1.5.31
- Ktor version bumped to 1.6.4

## [1.7.0] - August 14th, 2021

### Added

- Added ability to inject an emergency `UndeclaredField` in the event of certain polymorphic serializers and such

## [1.6.0] - August 12th, 2021

### Added

- Ability to add custom type schema overrides for edge case types.

## [1.5.1] - August 12th, 2021

### Changed

- Fixed bug where polymorphic types were not being rendered correctly when part of collections and maps

## [1.5.0] - July 25th, 2021

### Changed

- Added support for BigInteger and BigDecimal in response types

## [1.4.0] - July 22nd, 2021

### Changed

- Decreased jvmTarget version from 11 to 1.8

## [1.3.0] - June 4th, 2021

### Changed

- Explicitly encode JSON object by default

## [1.2.3] - June 3rd, 2021

### Added

- Updates showing/explaining serializer agnostic approach

## [1.2.2] - May 23rd, 2021

This is just to get my repo back to normal now that I have confirmed sonatype publish is happening

## [1.2.0] - May 23rd, 2021

### Added

- Finally, successfully pushed to Maven Central!!!

## [1.1.0] - May 19th, 2021

### Added

- Support for sealed classes 🔥
- Support for generic classes ☄️

## [1.0.1] - May 10th, 2021

### Changed

- a word to sweep my rude commit message to Nexus under the rug

## [1.0.0] - May 9th, 2021

### Added

- SonaType integration to publish to MavenCentral

### [1.0.0-rc] - May 8th, 2021

### Changed

- Migrated to io.bkbn group and package name

### [1.0.0-beta] - May 6th, 2021

### Added

- Release action to package a release JAR 🍻
- EXTREME DOCUMENTATION 📜

### Changed

- Cleanup to test files
- Removes KompendiumHttpCodes in favor of Ktor HttpStatusCode

### [0.9.0] - May 5th, 2021

### Added

- Support for default parameter responses

### Changed

- In order to facilitate default parameters, a couple changes were needed
  - `KompendiumParam` was added in replacement of the four parameter annotations
  - Specs now explicitly declare type of parameter rather than a reference in order to not override default values.

## [0.8.0] - May 4th, 2021

### Added

- Support for example request and response bodies. Parameter examples / defaults are a separate issue for later.

### Changed

- Converted `MethodInfo` into a sealed class with distinct method types for Get, Post, Put, and Delete

## [0.7.0] - April 29th, 2021

### Added

- `notarizedException` for notarizing `StatusPage` handlers 🎉
- `com.adarshr.test-logger` Gradle plugin for improved test output clarity and insight

### Changed

- Refactored `kompendium-core` to break up the `Kompendium` object into slightly more manageable chunks
- Notarization Parameters can now be inferred from method info

## [0.6.2] - April 23rd, 2021

### Added

- Request params are not required when property is nullable

## [0.6.1] - April 23rd, 2021

### Added

- Added support for Swagger ui

### Changed

- Set jvm target to 11
- Resolved bug for empty params and/or empty response body

## [0.6.0] - April 21st, 2021

### Added

- Added basic and jwt security scheme support with the new module kompendium-auth

## [0.5.2] - April 19th, 2021

### Removed

- Removed `Route.calculatePath`

### Added

- Added an explicit  `PathCalculator` interface to allow for easier handling of routes external to the core set of Ktor
  route selectors.

## [0.5.1] - April 19th, 2021

### Changed

- Resolved bug where paths under root route where appending a trailing `/`

## [0.5.0] - April 19th, 2021

### Added

- Expose `/openapi.json` and `/docs` as opt-in pre-built Routes

## [0.4.0] - April 17th, 2021

### Added

- Basic Query and Path Parameter Support 🍻

### Changed

- No content workaround, flow will likely need refactoring for clarity.

## [0.3.0] - April 17th, 2021

### Changed

- Removed response and request annotations in favor of MethodInfo extension.
- Modified notarization to add the correct reference slug regardless of type

## [0.2.0] - April 16th, 2021

### Changed

- Another re-haul to the reflection analysis
- Top level generics, enums, collections, and maps now supported 🙌

## [0.1.1] - April 16th, 2021

### Added

- Explicit UUID support to prevent incorrect interpretation as complex object

## [0.1.0] - April 16th, 2021

### Changed

- Completely redid the reflection system to improve flow, decrease errors ✨

### Added

- Added ReDoc to the Playground to make manual testing more convenient

## [0.0.7] - April 16th, 2021

### Added

- Include sources in publish 📚

## [0.0.6] - April 15th, 2021

### Added

- Logging to get a more intuitive sense for operations performed
- Serialization for Maps, Collections and Enums

## [0.0.5] - April 15th, 2021

### Added

- Full list of compile-time-constant status codes

## [0.0.4] - April 14th, 2021

### Changed

- Added fix to route selector matching that accounts for trailing slash selection

## [0.0.3] - April 13th, 2021

### Added

- Notarized Deletes
- Request and Response reflection abstractions
- Basic unit test coverage for each notarized operation

## [0.0.2] - April 12th, 2021

### Added

- Beginning of an implementation. Currently, able to generate a rough outline of the API at runtime, along with
  generating full data classes represented by JSON Schema.

## [0.0.1] - April 11th, 2021

### Added

- Added _most_ of the data classes necessary for generating an [Open API Spec](https://swagger.io/specification)
- Added playground to allow users to tinker with a live Ktor api in conjunction with development
- Added all standard OSS files
- Absolutely paltry README
- Added License
