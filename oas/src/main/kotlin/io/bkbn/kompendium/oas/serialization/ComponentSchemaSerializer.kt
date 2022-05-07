package io.bkbn.kompendium.oas.serialization

import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ComponentSchema::class)
object ComponentSchemaSerializer : KSerializer<ComponentSchema> {
  override fun deserialize(decoder: Decoder): ComponentSchema {
    error("Abandon all hope ye who enter 💀")
  }

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ComponentSchema", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ComponentSchema) {
    when (value) {
      is AnyOfSchema -> AnyOfSchema.serializer().serialize(encoder, value)
      is ReferencedSchema -> ReferencedSchema.serializer().serialize(encoder, value)
      is ArraySchema -> ArraySchema.serializer().serialize(encoder, value)
      is DictionarySchema -> DictionarySchema.serializer().serialize(encoder, value)
      is EnumSchema -> EnumSchema.serializer().serialize(encoder, value)
      is FormattedSchema -> FormattedSchema.serializer().serialize(encoder, value)
      is FreeFormSchema -> FreeFormSchema.serializer().serialize(encoder, value)
      is ObjectSchema -> ObjectSchema.serializer().serialize(encoder, value)
      is SimpleSchema -> SimpleSchema.serializer().serialize(encoder, value)
    }
  }

}
