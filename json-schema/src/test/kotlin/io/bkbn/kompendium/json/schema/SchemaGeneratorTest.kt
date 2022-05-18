package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.SimpleEnum
import io.bkbn.kompendium.core.fixtures.TestResponse
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
      val schema = SchemaGenerator.fromType<Int>()

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
  }
  describe("Objects") {
    it("Can generate the schema for a complex object") {
      // act
      val schema = SchemaGenerator.fromType<ComplexRequest>()

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
  }
  describe("Enums") {
    it("Can generate the schema for a simple enum") {
      // act
      val schema = SchemaGenerator.fromType<SimpleEnum>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "enum": [ "ONE", "TWO" ]
          }
        """.trimIndent()
      )
    }
  }
  describe("Arrays") {
    it("Can generate the schema for an array of scalars") {
      // act
      val schema = SchemaGenerator.fromType<List<Int>>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "items": {
              "type": "number",
              "format": "int32"
            },
            "type": [ "array" ]
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for an array of objects") {
      // act
      val schema = SchemaGenerator.fromType<List<TestResponse>>()

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
            "type": [ "array" ]
          }
        """.trimIndent()
      )
    }
    it("Can generate the schema for a nullable array") {
      // act
      val schema = SchemaGenerator.fromType<List<Int>?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "items": {
              "type": "number",
              "format": "int32"
            },
            "type": [ "null", "array" ]
          }
        """.trimIndent()
      )
    }
  }
  describe("Maps") {
    it("Can generate the schema for a map of scalars") {
      // act
      val schema = SchemaGenerator.fromType<Map<String, Int>>()

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
      shouldThrow<IllegalArgumentException> { SchemaGenerator.fromType<Map<Int, Int>>() }
    }
    it("Can generate the schema for a map of objects") {
      // act
      val schema = SchemaGenerator.fromType<Map<String, TestResponse>>()

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
      val schema = SchemaGenerator.fromType<Map<String, Int>?>()

      // assert
      schema.serialize() shouldEqualJson asJson(
        """
          {
            "additionalProperties": {
              "type": "number",
              "format": "int32"
            },
            "type": [ "null","object"]
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
