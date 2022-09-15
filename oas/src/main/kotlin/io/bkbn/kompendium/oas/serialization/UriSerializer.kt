package io.bkbn.kompendium.oas.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI

object UriSerializer : KSerializer<URI> {
  override fun deserialize(decoder: Decoder): URI = URI.create(decoder.decodeString())

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: URI) {
    encoder.encodeString(value.toString())
  }
}
