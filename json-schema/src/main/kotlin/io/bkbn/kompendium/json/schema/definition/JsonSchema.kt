package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = JsonSchema.Serializer::class)
sealed interface JsonSchema {

  val description: String?
  val deprecated: Boolean

  object Serializer : KSerializer<JsonSchema> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JsonSchema", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): JsonSchema {
      error("Abandon all hope ye who enter 💀")
    }

    override fun serialize(encoder: Encoder, value: JsonSchema) {
      when (value) {
        is ReferenceDefinition -> ReferenceDefinition.serializer().serialize(encoder, value)
        is TypeDefinition -> TypeDefinition.serializer().serialize(encoder, value)
        is EnumDefinition -> EnumDefinition.serializer().serialize(encoder, value)
        is ArrayDefinition -> ArrayDefinition.serializer().serialize(encoder, value)
        is MapDefinition -> MapDefinition.serializer().serialize(encoder, value)
        is NullableDefinition -> NullableDefinition.serializer().serialize(encoder, value)
        is OneOfDefinition -> OneOfDefinition.serializer().serialize(encoder, value)
        is AnyOfDefinition -> AnyOfDefinition.serializer().serialize(encoder, value)
      }
    }
  }
}
