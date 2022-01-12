package io.bkbn.kompendium.playground.util

import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Contact
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.info.License
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.oas.server.Server
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.net.URI

@OptIn(ExperimentalSerializationApi::class)
object Util {
  val kotlinxConfig = Json {
    classDiscriminator = "class"
    serializersModule = KompendiumSerializersModule.module
    prettyPrint = true
    explicitNulls = false
    encodeDefaults = true
  }

  val baseSpec = OpenApiSpec(
    info = Info(
      title = "Simple Demo API",
      version = "1.33.7",
      description = "Wow isn't this cool?",
      termsOfService = URI("https://example.com"),
      contact = Contact(
        name = "Homer Simpson",
        email = "chunkylover53@aol.com",
        url = URI("https://gph.is/1NPUDiM")
      ),
      license = License(
        name = "MIT",
        url = URI("https://github.com/bkbnio/kompendium/blob/main/LICENSE")
      )
    ),
    servers = mutableListOf(
      Server(
        url = URI("https://myawesomeapi.com"),
        description = "Production instance of my API"
      ),
      Server(
        url = URI("https://staging.myawesomeapi.com"),
        description = "Where the fun stuff happens"
      )
    )
  )
}
