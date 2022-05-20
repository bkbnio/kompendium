package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.SimpleEnum
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language

class SchemaGeneratorTest : DescribeSpec({
  describe("Scalars") {
    it("Can generate the schema for an Int") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<Int>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "type": "number",
            "format": "int32"
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a Boolean") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<Boolean>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "type": "boolean"
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a String") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<String>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "type": "string"
          }
        """.trimIndent()
      )
    }
  }
  describe("Objects") {
    it("Can generate the schema for a simple object") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<TestSimpleRequest>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "type": "object",
            "properties": {
              "a": {
                "type": "string"
              },
              "b": {
                "type": "number",
                "format": "int32"
              }
            },
            "required": [
              "a",
              "b"
            ]
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a complex object") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<ComplexRequest>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "type": "object",
            "properties": {
              "amazingField": {
                "type": "string"
              },
              "org": {
                "type": "string"
              },
              "tables": {
                "items": {
                  "type": "object",
                  "properties": {
                    "alias": {
                      "additionalProperties": {
                        "type": "object",
                        "properties": {
                          "enumeration": {
                            "enum": [
                              "ONE",
                              "TWO"
                            ]
                          }
                        },
                        "required": [
                          "enumeration"
                        ]
                      },
                      "type": "object"
                    },
                    "name": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "alias",
                    "name"
                  ]
                },
                "type": "array"
              }
            },
            "required": [
              "amazingField",
              "org",
              "tables"
            ]
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a nullable object") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<TestSimpleRequest?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "oneOf": [
              {
                "type": "null"
              },
              {
                "type": "object",
                "properties": {
                  "a": {
                    "type": "string"
                  },
                  "b": {
                    "type": "number",
                    "format": "int32"
                  }
                },
                "required": [
                  "a",
                  "b"
                ]
              }
            ]
          }
        """.trimIndent()
      )
    }
  }
  describe("Enums") {
    it("Can generate the schema for a simple enum") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<SimpleEnum>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "enum": [ "ONE", "TWO" ]
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a nullable enum") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<SimpleEnum?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "oneOf": [
              {
                "type": "null"
              },
              {
                "enum": [
                  "ONE",
                  "TWO"
                ]
              }
            ]
          }
        """.trimIndent()
      )
    }
  }
  describe("Arrays") {
    it("Can generate the schema for an array of scalars") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<List<Int>>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "items": {
              "type": "number",
              "format": "int32"
            },
            "type": "array"
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for an array of objects") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<List<TestResponse>>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "items": {
              "type": "object",
              "properties": {
                "c": {
                  "type": "string"
                }
              },
              "required": [
                "c"
              ]
            },
            "type": "array"
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a nullable array") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<List<Int>?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "oneOf": [
              {
                "type": "null"
              },
              {
                "items": {
                  "type": "number",
                  "format": "int32"
                },
                "type": "array"
              }
            ]
          }
        """.trimIndent()
      )
    }
  }
  describe("Maps") {
    it("Can generate the schema for a map of scalars") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<Map<String, Int>>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "additionalProperties": {
              "type": "number",
              "format": "int32"
            },
            "type": "object"
          }
        """.trimIndent()
      )
    }
    it("Throws an error when map keys are not strings") {
      // assert
      shouldThrow<IllegalArgumentException> { SchemaGenerator.fromTypeToSchema<Map<Int, Int>>() }
    }
    it("Can generate the schema for a map of objects") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<Map<String, TestResponse>>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "additionalProperties": {
              "type": "object",
              "properties": {
                "c": {
                  "type": "string"
                }
              },
              "required": [
                "c"
              ]
            },
            "type": "object"
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a nullable map") {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<Map<String, Int>?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "oneOf": [
              {
                "type": "null"
              },
              {
                "additionalProperties": {
                  "type": "number",
                  "format": "int32"
                },
                "type": "object"
              }
            ]
          }
        """.trimIndent()
      )
    }
  }
}) {
  companion object {
    private val json = Json {
      encodeDefaults = true
      explicitNulls = false
      prettyPrint = true
    }

    fun asJson(@Language("json") input: String) = input
    fun JsonSchema.serialize() = json.encodeToString(JsonSchema.serializer(), this)
  }
}
