package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("component_type") // todo figure out a way to filter this
sealed interface ComponentSchema {
  val description: String?
    get() = null

  val default: Any?
    get() = null

  fun addDefault(default: Any?): ComponentSchema = when (this) {
    is AnyOfSchema -> error("Cannot add default to anyOf reference") // todo is this true though?
    is ArraySchema -> this.copy(default = default)
    is DictionarySchema -> this.copy(default = default)
    is EnumSchema -> this.copy(default = default)
    is FormattedSchema -> this.copy(default = default)
    is ObjectSchema -> this.copy(default = default)
    is SimpleSchema -> this.copy(default = default)
    is ReferencedSchema -> this.copy(default = default)
    is FreeFormSchema -> this.copy(default = default)
    is EmptySchema -> error("Cannot add default to an empty schema")
    else -> error("Compiler bug??")
  }

  fun setDescription(description: String): ComponentSchema = when (this) {
    is AnyOfSchema -> this.copy(description = description)
    is ArraySchema -> this.copy(description = description)
    is DictionarySchema -> this.copy(description = description)
    is EnumSchema -> this.copy(description = description)
    is FormattedSchema -> this.copy(description = description)
    is ObjectSchema -> this.copy(description = description)
    is SimpleSchema -> this.copy(description = description)
    is ReferencedSchema -> this.copy(description = description)
    is FreeFormSchema -> this.copy(description = description)
    is EmptySchema -> error("Cannot add description to an empty schema")
    else -> error("Compiler bug??")
  }
}
