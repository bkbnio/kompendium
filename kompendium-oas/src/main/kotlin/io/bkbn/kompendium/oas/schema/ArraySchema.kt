package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ArraySchema(
  val items: ComponentSchema,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // constraints
  val minItems: Int? = null,
  val maxItems: Int? = null,
  val uniqueItems: Boolean? = null,
  val xml: Xml? = null,
) : TypedSchema {
  override val type: String = "array"

  @Serializable
  data class Xml(
    val name: String? = null,
    val namespace: String? = null,
    val prefix: String? = null,
    val attribute: Boolean? = null,
    val wrapped: Boolean? = null,
  )
}
