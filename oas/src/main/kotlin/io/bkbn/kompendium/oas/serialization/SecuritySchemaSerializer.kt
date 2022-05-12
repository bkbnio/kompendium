package io.bkbn.kompendium.oas.serialization

import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.bkbn.kompendium.oas.security.SecuritySchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = SecuritySchema::class)
object SecuritySchemaSerializer : KSerializer<SecuritySchema> {
  override fun deserialize(decoder: Decoder): SecuritySchema {
    error("Abandon all hope ye who enter 💀")
  }

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SecuritySchema", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: SecuritySchema) {
    when (value) {
      is ApiKeyAuth -> ApiKeyAuth.serializer().serialize(encoder, value)
      is BasicAuth -> BasicAuth.serializer().serialize(encoder, value)
      is BearerAuth -> BearerAuth.serializer().serialize(encoder, value)
      is OAuth -> OAuth.serializer().serialize(encoder, value)
    }
  }
}
