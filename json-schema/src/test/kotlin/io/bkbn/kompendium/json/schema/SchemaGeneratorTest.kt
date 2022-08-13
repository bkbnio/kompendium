package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.FlibbityGibbit
import io.bkbn.kompendium.core.fixtures.SimpleEnum
import io.bkbn.kompendium.core.fixtures.SlammaJamma
import io.bkbn.kompendium.core.fixtures.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json

class SchemaGeneratorTest : DescribeSpec({
  describe("Scalars") {
    it("Can generate the schema for an Int") {
      jsonSchemaTest<Int>("T0001__scalar_int.json")
    }
    it("Can generate the schema for a Boolean") {
      jsonSchemaTest<Boolean>("T0002__scalar_bool.json")
    }
    it("Can generate the schema for a String") {
      jsonSchemaTest<String>("T0003__scalar_string.json")
    }
  }
  describe("Objects") {
    it("Can generate the schema for a simple object") {
      jsonSchemaTest<TestSimpleRequest>("T0004__simple_object.json")
    }
    it("Can generate the schema for a complex object") {
      jsonSchemaTest<ComplexRequest>("T0005__complex_object.json")
    }
    it("Can generate the schema for a nullable object") {
      jsonSchemaTest<TestSimpleRequest?>("T0006__nullable_object.json")
    }
    it("Can generate the schema for a polymorphic object") {
      jsonSchemaTest<FlibbityGibbit>("T0015__polymorphic_object.json")
    }
    xit("Can generate the schema for a recursive type") {
       // TODO jsonSchemaTest<SlammaJamma>("T0016__recursive_object.json")
    }
  }
  describe("Enums") {
    it("Can generate the schema for a simple enum") {
      jsonSchemaTest<SimpleEnum>("T0007__simple_enum.json")
    }
    it("Can generate the schema for a nullable enum") {
      jsonSchemaTest<SimpleEnum?>("T0008__nullable_enum.json")
    }
  }
  describe("Arrays") {
    it("Can generate the schema for an array of scalars") {
      jsonSchemaTest<List<Int>>("T0009__scalar_array.json")
    }
    it("Can generate the schema for an array of objects") {
      jsonSchemaTest<List<TestResponse>>("T0010__object_array.json")
    }
    it("Can generate the schema for a nullable array") {
      jsonSchemaTest<List<Int>?>("T0011__nullable_array.json")
    }
  }
  describe("Maps") {
    it("Can generate the schema for a map of scalars") {
      jsonSchemaTest<Map<String, Int>>("T0012__scalar_map.json")
    }
    it("Throws an error when map keys are not strings") {
      shouldThrow<IllegalArgumentException> { SchemaGenerator.fromTypeToSchema<Map<Int, Int>>() }
    }
    it("Can generate the schema for a map of objects") {
      jsonSchemaTest<Map<String, TestResponse>>("T0013__object_map.json")
    }
    it("Can generate the schema for a nullable map") {
      jsonSchemaTest<Map<String, Int>?>("T0014__nullable_map.json")
    }
  }
}) {
  companion object {
    private val json = Json {
      encodeDefaults = true
      explicitNulls = false
      prettyPrint = true
    }

    private fun JsonSchema.serialize() = json.encodeToString(JsonSchema.serializer(), this)

    private inline fun <reified T> jsonSchemaTest(snapshotName: String) {
      // act
      val schema = SchemaGenerator.fromTypeToSchema<T>()

      // todo add cache assertions!!!

      // assert
      schema.serialize() shouldEqualJson getFileSnapshot(snapshotName)
    }
  }
}
