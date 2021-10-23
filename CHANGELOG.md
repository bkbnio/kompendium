# Changelog

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

- Support for example request and response bodies.  Parameter examples / defaults are a separate issue for later.

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

- Added an explicit  `PathCalculator` interface to allow for easier handling of routes external to the core set of Ktor route selectors.

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

- Beginning of an implementation.  Currently, able to generate a rough outline of the API at runtime, along with generating 
full data classes represented by JSON Schema.  

## [0.0.1] - April 11th, 2021

### Added
- Added _most_ of the data classes necessary for generating an [Open API Spec](https://swagger.io/specification)
- Added playground to allow users to tinker with a live Ktor api in conjunction with development
- Added all standard OSS files
- Absolutely paltry README
- Added License
