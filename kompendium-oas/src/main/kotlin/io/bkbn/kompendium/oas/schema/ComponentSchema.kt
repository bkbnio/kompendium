package io.bkbn.kompendium.oas.schema

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
    else -> error("Compiler bug??")
  }

  fun setDescription(description: String) = when (this) {
    is AnyOfSchema -> this.copy(description = description)
    is ArraySchema -> this.copy(description = description)
    is DictionarySchema -> this.copy(description = description)
    is EnumSchema -> this.copy(description = description)
    is FormattedSchema -> this.copy(description = description)
    is ObjectSchema -> this.copy(description = description)
    is SimpleSchema -> this.copy(description = description)
    else -> error("Compiler bug??")
  }
}
