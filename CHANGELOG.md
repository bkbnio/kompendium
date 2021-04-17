# Changelog

## [0.3.0] - April 17th, 2021

### Changed

- Removed response and request annotations in favor of MethodInfo extension.

## [0.2.1] - April 16th, 2021

### Changed

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
