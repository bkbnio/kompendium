package io.bkbn.kompendium.oas.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NumberSerializer : KSerializer<Number> {
  override fun deserialize(decoder: Decoder): Number = try {
    decoder.decodeDouble()
  } catch (_: SerializationException) {
    decoder.decodeInt()
  }

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Number) {
    encoder.encodeString(value.toString())
  }

}
