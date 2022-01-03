package oas.schema

import oas.schema.ComponentSchema

sealed interface TypedSchema : ComponentSchema {
  val type: String
  val nullable: Boolean?
  override val default: Any?
}
