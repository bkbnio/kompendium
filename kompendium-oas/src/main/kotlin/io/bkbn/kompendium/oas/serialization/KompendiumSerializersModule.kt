package io.bkbn.kompendium.oas.serialization

import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import io.bkbn.kompendium.oas.security.ApiKeyAuth
import io.bkbn.kompendium.oas.security.BasicAuth
import io.bkbn.kompendium.oas.security.BearerAuth
import io.bkbn.kompendium.oas.security.OAuth
import io.bkbn.kompendium.oas.security.SecuritySchema
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object KompendiumSerializersModule {

  val module = SerializersModule {
    polymorphic(ComponentSchema::class) {
      subclass(SimpleSchema::class, SimpleSchema.serializer())
      subclass(FormattedSchema::class, FormattedSchema.serializer())
      subclass(ObjectSchema::class, ObjectSchema.serializer())
      subclass(AnyOfSchema::class, AnyOfSchema.serializer())
      subclass(ArraySchema::class, ArraySchema.serializer())
      subclass(DictionarySchema::class, DictionarySchema.serializer())
      subclass(EnumSchema::class, EnumSchema.serializer())
      subclass(FreeFormSchema::class, FreeFormSchema.serializer())
    }
    polymorphic(SecuritySchema::class) {
      subclass(ApiKeyAuth::class, ApiKeyAuth.serializer())
      subclass(BasicAuth::class, BasicAuth.serializer())
      subclass(BearerAuth::class, BearerAuth.serializer())
      subclass(OAuth::class, OAuth.serializer())
    }
    contextual(Any::class, AnySerializer())
  }

}
