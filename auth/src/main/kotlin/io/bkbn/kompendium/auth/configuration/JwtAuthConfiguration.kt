package io.bkbn.kompendium.auth.configuration

interface JwtAuthConfiguration : SecurityConfiguration {
  val bearerFormat: String
    get() = "JWT"
}
