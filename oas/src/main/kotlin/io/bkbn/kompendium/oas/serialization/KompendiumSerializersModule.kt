package io.bkbn.kompendium.oas.serialization

import kotlinx.serialization.modules.SerializersModule

object KompendiumSerializersModule {
  val module = SerializersModule {
    contextual(Any::class, AnySerializer())
  }
}
