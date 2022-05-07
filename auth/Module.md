# Module kompendium-auth

This module is responsible for providing wrappers around ktor-auth configuration blocks, allowing users to document
their API authentication with minimal modifications to their existing configuration.

# Package io.bkbn.kompendium.auth

Base package that is responsible for setting up required authentication route handlers along with exposing wrapper
methods for each ktor-auth authentication mechanism.

# Package io.bkbn.kompendium.auth

Houses the available security configurations. At the moment, `Basic`, `JWT`, `ApiKey`, and `OAuth` are supported
