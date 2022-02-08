package io.bkbn.kompendium.oas.serialization

import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

class AnySerializer<T : Any> : KSerializer<T> {
  override fun serialize(encoder: Encoder, value: T) {
    serialize(encoder, value, value::class as KClass<T>)
  }

  override fun deserialize(decoder: Decoder): T {
    error("Abandon all hope ye who enter 💀")
  }

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KompendiumAny", PrimitiveKind.STRING)

  @OptIn(InternalSerializationApi::class)
  fun serialize(encoder: Encoder, obj: T, clazz: KClass<T>) {
    clazz.serializer().serialize(encoder, obj)
  }
}
