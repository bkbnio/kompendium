package io.bkbn.kompendium.oas.schema

sealed interface TypedSchema : ComponentSchema {
  val type: String
  override val default: Any?
}
