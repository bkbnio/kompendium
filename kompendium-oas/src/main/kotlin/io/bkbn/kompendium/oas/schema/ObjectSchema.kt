package io.bkbn.kompendium.oas.schema

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ObjectSchema(
  val properties: Map<String, ComponentSchema>,
  override val default: @Contextual Any? = null,
  override val description: String? = null,
  override val nullable: Boolean? = null,
  // constraints
  val required: List<String>? = null
) : TypedSchema {
  override val type = "object"

  override fun equals(other: Any?): Boolean {
    if (other !is ObjectSchema) return false
    if (properties != other.properties) return false
    if (description != other.description) return false
    // TODO Going to need some way to differentiate nullable vs non-nullable reference schemas 😬
//    if (nullable != other.nullable) return false
    if (required != other.required) return false
    return true
  }

  override fun hashCode(): Int {
    var result = properties.hashCode()
    result = 31 * result + (default?.hashCode() ?: 0)
    result = 31 * result + (description?.hashCode() ?: 0)
    result = 31 * result + (nullable?.hashCode() ?: 0)
    result = 31 * result + (required?.hashCode() ?: 0)
    result = 31 * result + type.hashCode()
    return result
  }
}
