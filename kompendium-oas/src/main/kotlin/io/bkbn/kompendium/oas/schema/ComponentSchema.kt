package io.bkbn.kompendium.oas.schema

sealed interface ComponentSchema {
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
  }
}
