# Changelog

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
