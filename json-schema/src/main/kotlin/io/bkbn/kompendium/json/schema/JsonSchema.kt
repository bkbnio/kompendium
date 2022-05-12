package io.bkbn.kompendium.json.schema

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = JsonSchema.Serializer::class)
sealed interface JsonSchema {
  object Serializer : KSerializer<JsonSchema> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JsonSchema", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): JsonSchema {
      error("Abandon all hope ye who enter 💀")
    }

    override fun serialize(encoder: Encoder, value: JsonSchema) {
      when (value) {
        is ReferenceSchema -> ReferenceSchema.serializer().serialize(encoder, value)
        is TypeDefinition -> TypeDefinition.serializer().serialize(encoder, value)
      }
    }

  }
}
