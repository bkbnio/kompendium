package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SimpleSchema(
  override val type: String,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // Constraints
  val minLength: Int? = null,
  val maxLength: Int? = null,
  val pattern: String? = null,
  val format: String? = null,
  val xml: Xml? = null,
  ) : TypedSchema {

    @Serializable
    data class Xml(
      val name: String? = null,
      val namespace: String? = null,
      val prefix: String? = null,
      val attribute: Boolean? = null,
    )
  }
