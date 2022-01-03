package io.bkbn.kompendium.oas.schema

sealed interface TypedSchema : ComponentSchema {
  val type: String
  val nullable: Boolean?
  override val default: Any?
}
