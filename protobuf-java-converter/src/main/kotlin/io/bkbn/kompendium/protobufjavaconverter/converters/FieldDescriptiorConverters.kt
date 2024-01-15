package io.bkbn.kompendium.protobufjavaconverter.converters

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.MapDefinition
import io.bkbn.kompendium.json.schema.definition.NullableDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.json.schema.util.Helpers
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Extension function to generate all custom TypeDefinitions for.
 *
 * Traverses all fields and subfields of the message it also generates references for nested messages.
 *
 * @property GeneratedMessageV3 the protobuf message to generate all the custom definitions for
 * @param snakeCase whether to convert the names to snake case or not
 * @return a [Map] of ([KType], [TypeDefinition]) to be used in `customTypes` in [NoterizedApplication]
 *
 */
fun GeneratedMessageV3.createCustomTypesForTypeAndSubTypes(snakeCase: Boolean = false): Map<KType, JsonSchema> {
  val cache: MutableMap<String, JsonSchema> = mutableMapOf()
  return mapOf(
    this::class.createType() to TypeDefinition(
      type = "object",
      properties = toJsonSchemaMap(this.descriptorForType, cache, snakeCase)
    )
  )
    // Dont forget to add the definitions for our references
    .plus(
      cache.map {
        // Get the class from the respective key (which should be a fullname of the class
        val clazz = Class.forName(it.key)
        clazz.kotlin.createType() to it.value
      }.toMap()
    )
}

/**
 * Loops over all fields of the provided Descriptor to discover the field types
 *
 * @param protoDescriptor the [Descriptors.FieldDescriptor] to convert
 * @param cache map of cached definitions
 * @param snakeCase whether to convert the names to snake case or not
 * @return a [Map] of ([String], [JsonSchema])
 *
 */
fun toJsonSchemaMap(
  protoDescriptor: Descriptors.Descriptor,
  cache: MutableMap<String, JsonSchema> = mutableMapOf(),
  snakeCase: Boolean = false
): Map<String, JsonSchema> =
  protoDescriptor.fields.map {
    val key = if (snakeCase) it.jsonName.toSnakeCase() else it.jsonName
    key to fromNestedTypeToSchema(it, cache)
  }.toMap()

/**
 * Very simple snake case conversion
 */
fun String.toSnakeCase() =
  this.map {
    if (it.isUpperCase()) "_${it.lowercase()}" else it.toString()
  }.reduce { acc, s -> acc + s }

/**
 * Converts a field from a proto message to a JsonSchema
 *
 * @param javaProtoField the [Descriptors.FieldDescriptor] to convert
 * @param cache map of cached definitions
 * @return the resulting [JsonSchema]
 *
 */
fun fromNestedTypeToSchema(
  javaProtoField: Descriptors.FieldDescriptor,
  cache: MutableMap<String, JsonSchema> = mutableMapOf()
): JsonSchema =
  when {
    javaProtoField.isRepeated && !javaProtoField.isMapField -> ArrayDefinition(fromTypeToSchema(javaProtoField, cache))
    javaProtoField.isMapField -> handleMapField(javaProtoField, cache)
    else -> fromTypeToSchema(javaProtoField, cache)
  }

/**
 * Converts a map field descriptor
 *
 * It generates some key examples based on the type provided and gets the TypeDefinition of the value.
 *
 * @param javaProtoField the field to convert (which should be a mapField)
 * @param cache map of cached definitions
 * @return returns the a [MapDefinition] schema with additional properties to describe the key and value
 */
@Suppress("MagicNumber")
fun handleMapField(
  javaProtoField: Descriptors.FieldDescriptor,
  cache: MutableMap<String, JsonSchema> = mutableMapOf()
): JsonSchema {
  require(javaProtoField.isMapField) { "Should never be called for a non map type" }

  val keyField = javaProtoField.containingType.nestedTypes.first().findFieldByName("key")
  val valueField = javaProtoField.containingType.nestedTypes.first().findFieldByName("value")
  val valueType = fromTypeToSchema(valueField, cache)
  // Keys can only be string in json but we can still have "0", "0.0" or "true", "ENUM_VALUE" as keys
  val keys: List<String> = when (keyField.javaType) {
    Descriptors.FieldDescriptor.JavaType.INT,
    Descriptors.FieldDescriptor.JavaType.LONG -> (0..1).map { it.toString() }
    Descriptors.FieldDescriptor.JavaType.FLOAT,
    Descriptors.FieldDescriptor.JavaType.DOUBLE -> listOf(0.0, 0.1, 0.2).map { it.toString() }
    Descriptors.FieldDescriptor.JavaType.BOOLEAN -> listOf(true, false).map { it.toString() }
    Descriptors.FieldDescriptor.JavaType.STRING -> (0..1).map { "myVariable$it" }
    Descriptors.FieldDescriptor.JavaType.BYTE_STRING -> (0..1).map { "0x$it" }
    Descriptors.FieldDescriptor.JavaType.ENUM -> (0..1).map { "ENUM_VALUE$it" }
    null,
    Descriptors.FieldDescriptor.JavaType.MESSAGE -> throw IllegalArgumentException("Cant use object as key")
  }
  return MapDefinition(
    TypeDefinition(
      type = "object",
      properties = keys.map { it to valueType }.toMap()
    )
  )
}

/**
 * Converts scalar, enum and message type descriptors to TypeDefinitions
 *
 * @param javaProtoField the field to convert
 * @param cache map of cached definitions
 * @return returns a matching [JsonSchema]
 */
fun fromTypeToSchema(
  javaProtoField: Descriptors.FieldDescriptor,
  cache: MutableMap<String, JsonSchema> = mutableMapOf()
): JsonSchema {
  checkTypeCache(javaProtoField, cache)?.let { return it }
  return when (javaProtoField.javaType) {
    Descriptors.FieldDescriptor.JavaType.INT -> TypeDefinition.INT
    Descriptors.FieldDescriptor.JavaType.LONG -> TypeDefinition.LONG
    Descriptors.FieldDescriptor.JavaType.FLOAT -> TypeDefinition.FLOAT
    Descriptors.FieldDescriptor.JavaType.DOUBLE -> TypeDefinition.DOUBLE
    Descriptors.FieldDescriptor.JavaType.BOOLEAN -> TypeDefinition.BOOLEAN
    Descriptors.FieldDescriptor.JavaType.STRING -> TypeDefinition.STRING
    Descriptors.FieldDescriptor.JavaType.BYTE_STRING -> TypeDefinition.STRING
    Descriptors.FieldDescriptor.JavaType.ENUM -> {
      cache[javaProtoField.enumType.fullName] = EnumDefinition(
        enum = javaProtoField.enumType.values.map { it.name }.toSet()
      )
      ReferenceDefinition("${Helpers.COMPONENT_SLUG}/${javaProtoField.enumType.name}")
    }
    Descriptors.FieldDescriptor.JavaType.MESSAGE -> {
      // Traverse through possible nested messages
      cache[javaProtoField.messageType.fullName] = TypeDefinition(
        type = "object",
        properties = javaProtoField.messageType.fields.map {
          it.jsonName to fromNestedTypeToSchema(it, cache)
        }.toMap()
      )
      ReferenceDefinition("${Helpers.COMPONENT_SLUG}/${javaProtoField.messageType.name}")
    }
    null -> NullableDefinition()
  }
}

fun checkTypeCache(
  javaProtoField: Descriptors.FieldDescriptor,
  cache: MutableMap<String, JsonSchema>
): JsonSchema? =
  when (javaProtoField.javaType) {
    Descriptors.FieldDescriptor.JavaType.ENUM -> cache[javaProtoField.enumType.name]
    Descriptors.FieldDescriptor.JavaType.MESSAGE -> cache[javaProtoField.messageType.name]
    else -> null
  }
