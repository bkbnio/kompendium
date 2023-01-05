package io.bkbn.kompendium.json.schema.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import kotlin.Number as KNumber

object Serializers {

  object Uuid : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
      return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
      encoder.encodeString(value.toString())
    }
  }

  object Number : KSerializer<KNumber> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): KNumber {
      TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: KNumber) {
      when (value) {
        is Int -> encoder.encodeInt(value)
        is Long -> encoder.encodeLong(value)
        is Double -> encoder.encodeDouble(value)
        is Float -> encoder.encodeFloat(value)
        else -> throw IllegalArgumentException("Number is not a valid type")
      }
    }
  }
}
