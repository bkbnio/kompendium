package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class EnumSchema(
  val enum: Set<String>,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null
) : TypedSchema {
  override val type: String = "string"

  override fun equals(other: Any?): Boolean {
    if (other !is EnumSchema) return false
    if (enum != other.enum) return false
    // TODO Going to need some way to differentiate nullable vs non-nullable reference schemas 😬
    // if (nullable != other.nullable) return false
    return true
  }

  override fun hashCode(): Int {
    var result = enum.hashCode()
    result = 31 * result + (default?.hashCode() ?: 0)
    result = 31 * result + (description?.hashCode() ?: 0)
    result = 31 * result + (nullable?.hashCode() ?: 0)
    result = 31 * result + type.hashCode()
    return result
  }
}
