package io.bkbn.kompendium.protobufjavaconverter.converters

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import io.bkbn.kompendium.json.schema.definition.ArrayDefinition
import io.bkbn.kompendium.json.schema.definition.EnumDefinition
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.bkbn.kompendium.json.schema.definition.MapDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.protobufjavaconverter.Corpus
import io.bkbn.kompendium.protobufjavaconverter.DoubleNestedMessage
import io.bkbn.kompendium.protobufjavaconverter.NestedMapMessage
import io.bkbn.kompendium.protobufjavaconverter.EnumMessage
import io.bkbn.kompendium.protobufjavaconverter.GoogleTypes
import io.bkbn.kompendium.protobufjavaconverter.NestedMessage
import io.bkbn.kompendium.protobufjavaconverter.RepeatedEnumMessage
import io.bkbn.kompendium.protobufjavaconverter.RepeatedMessage
import io.bkbn.kompendium.protobufjavaconverter.SimpleMapMessage
import io.bkbn.kompendium.protobufjavaconverter.SimpleTestMessage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.matchers.types.shouldNotBeTypeOf
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class FieldDescriptiorConvertersKtTest : DescribeSpec({
  describe("fromTypeToSchemaTests") {
    val simpleMessageDescriptor = SimpleTestMessage.getDescriptor()
    it("java int field should return TypeDefinition INT") {
      listOf(
        "uint32",
        "int32",
        "sint32",
        "fixed32",
      ).forEach {
        fromTypeToSchema(simpleMessageDescriptor.findFieldByName("my_test_$it")).shouldBe(TypeDefinition.INT)
      }
    }
    it("long number type field should return TypeDefinition LONG") {
      listOf(
        "uint64",
        "int64",
        "sint64",
      ).forEach {
        val field = simpleMessageDescriptor.findFieldByName("my_test_$it")
        fromTypeToSchema(field).shouldBe(TypeDefinition.LONG)
      }
    }

    it("double field should return TypeDefinition DOUBLE") {
      listOf(
        "double",
      ).forEach {
        fromTypeToSchema(simpleMessageDescriptor.findFieldByName("my_test_$it")).shouldBe(TypeDefinition.DOUBLE)
      }
    }
    it("bool field should return TypeDefinition BOOLEAN") {
      listOf(
        "bool",
      ).forEach {
        fromTypeToSchema(simpleMessageDescriptor.findFieldByName("my_test_$it")).shouldBe(TypeDefinition.BOOLEAN)
      }
    }
    it("string fields should return TypeDefinition STRING }") {
      listOf(
        "string",
        "bytes",
      ).forEach {
        fromTypeToSchema(simpleMessageDescriptor.findFieldByName("my_test_$it")).shouldBe(TypeDefinition.STRING)
      }
    }

    it("Nested message should return ReferenceDefinition }") {
      val message = NestedMessage.getDescriptor()
      val result = fromNestedTypeToSchema(message.findFieldByName("nested_field"))
      result.shouldBeTypeOf<ReferenceDefinition>()
      result.`$ref`.shouldBe(message.findFieldByName("nested_field").messageType.name)
    }

    it("Repeated message should return ArrayDefinition") {
      val message = RepeatedMessage.getDescriptor()
      val result = fromNestedTypeToSchema(message.findFieldByName("repeated_field"))
      result.shouldBeTypeOf<ArrayDefinition>()
      result.items.shouldBeTypeOf<ReferenceDefinition>()
      (result.items as ReferenceDefinition).`$ref`.shouldBe(SimpleTestMessage.getDescriptor().name)
    }

    it("Repeated enum message should return ArrayDefinition") {
      val message: Descriptors.Descriptor = RepeatedEnumMessage.getDescriptor()
      val result: JsonSchema = fromNestedTypeToSchema(message.findFieldByName("repeated_field"))
      result.shouldBeTypeOf<ArrayDefinition>()
      result.items.shouldBeTypeOf<ReferenceDefinition>()
      (result.items as ReferenceDefinition).`$ref`.shouldBe(Corpus.getDescriptor().name)
    }

    it("SimpleMapMessage message should return MapDefinition") {
      val message = SimpleMapMessage.getDescriptor()
      val mapField = message.findFieldByName("map_field")
      val expectedValueTypeDefinition =
        fromNestedTypeToSchema(mapField.containingType.nestedTypes.first().findFieldByName("value"))
      val result = fromNestedTypeToSchema(mapField)
      result.shouldBeTypeOf<MapDefinition>()
      (result.additionalProperties as TypeDefinition).properties!!.entries.first().value.shouldBe(
        expectedValueTypeDefinition
      )
    }

    it("NestedMapMessage message should return MapDefinition") {
      val message = NestedMapMessage.getDescriptor()
      val mapField = message.findFieldByName("map_field")
      val expectedValueTypeDefinition =
        fromNestedTypeToSchema(mapField.containingType.nestedTypes.first().findFieldByName("value"))
      val result = fromNestedTypeToSchema(mapField)
      result.shouldBeTypeOf<MapDefinition>()
      (result.additionalProperties as TypeDefinition).properties!!.entries.first().value.shouldBe(
        expectedValueTypeDefinition
      )
    }

    it("GoogleType duration return Object") {
      val message = GoogleTypes.getDescriptor()
      fromTypeToSchema(message.findFieldByName("duration_field")).shouldBeTypeOf<ReferenceDefinition>()
    }

    it("GoogleType timestamp return Object") {
      val message = GoogleTypes.getDescriptor()
      fromTypeToSchema(message.findFieldByName("timestamp_field")).shouldBeTypeOf<ReferenceDefinition>()
    }
  }

  describe("from message to schema map test") {
    it("Should contain our simple message description") {
      val message = SimpleTestMessage.getDefaultInstance()
      val expectedType: KType = message::class.createType()
      val resultSchema = testMessageBasics(message)
      val expectedMapping = mapOf(
        expectedType to TypeDefinition(
          type = "object",
          properties = message.descriptorForType.fields?.map { it.jsonName to fromNestedTypeToSchema(it) }?.toMap()
        )
      )
      resultSchema.shouldContainExactly(expectedMapping)
    }

    it("Nested message to schema") {
      val message = NestedMessage.getDefaultInstance()
      val expectedType: KType = message::class.createType()
      val resultSchema = testMessageBasics(message)
      // We already tested all the separate field mappings and their types
      val expectedMapping = mapOf(
        // Expect the definition four our object
        expectedType to TypeDefinition(
          type = "object",
          properties = message.descriptorForType.fields?.map { it.jsonName to fromNestedTypeToSchema(it) }?.toMap()
        ),
        // Expect the definition for our nested object
        SimpleTestMessage::class.createType() to TypeDefinition(
          type = "object",
          properties = SimpleTestMessage.getDescriptor().fields?.map {
            it.jsonName to fromNestedTypeToSchema(it)
          }?.toMap()
        )
      )
      resultSchema.shouldContainExactly(expectedMapping)
      val result = (resultSchema[expectedType] as TypeDefinition).properties!!["nestedField"]
      // Our nested field should be a reference
      result.shouldBeTypeOf<ReferenceDefinition>()
      // Our nested field should be a reference to simplemessage
      result.`$ref`.shouldBe(SimpleTestMessage.getDescriptor().name)
    }

    it("Double nested message to schema") {
      val message = DoubleNestedMessage.getDefaultInstance()
      val expectedType: KType = message::class.createType()
      val resultSchema = testMessageBasics(message)
      // We already tested all the separate field mappings and their types
      val expectedMapping = mapOf(
        // Expect our object definition
        expectedType to TypeDefinition(
          type = "object",
          properties = message.descriptorForType.fields?.map { it.jsonName to fromNestedTypeToSchema(it) }?.toMap()
        ),
        // Expect the definition for our nested object
        SimpleTestMessage::class.createType() to TypeDefinition(
          type = "object",
          properties = SimpleTestMessage.getDescriptor().fields?.map { it.jsonName to fromNestedTypeToSchema(it) }
            ?.toMap()
        ),
        NestedMessage::class.createType() to TypeDefinition(
          type = "object",
          properties = NestedMessage.getDescriptor().fields?.map { it.jsonName to fromNestedTypeToSchema(it) }?.toMap()
        ),
      )
      // We expect 2 definitions one for our Message and one for our
      resultSchema.shouldContainExactly(expectedMapping)
      // Make sure both our message and nested message contain a reference
      val result = (resultSchema[expectedType] as TypeDefinition).properties!!["nestedField"]
      // Our nested field should be a reference
      result.shouldBeTypeOf<ReferenceDefinition>()
      // it should be a reference to our nested message
      result.`$ref`.shouldBe(NestedMessage.getDescriptor().name)
      val nestedResult = (resultSchema[NestedMessage::class.createType()] as TypeDefinition).properties!!["nestedField"]
      nestedResult.shouldBeTypeOf<ReferenceDefinition>()
      // Our nested message reference should be pointing to simpleTest message
      nestedResult.`$ref`.shouldBe(SimpleTestMessage.getDescriptor().name)
      // last but not least we should have definition for our SimpleTest message which is not a reference
      (resultSchema[SimpleTestMessage::class.createType()] as TypeDefinition).shouldNotBeTypeOf<ReferenceDefinition>()
    }

    it("Repeated message to schema") {
      val message = RepeatedMessage.getDefaultInstance()
      testMessageBasics(message)
    }

    it("Repeated enum message to schema") {
      val message = RepeatedEnumMessage.getDefaultInstance()
      testMessageBasics(message)
    }

    it("Enum message to schema") {
      val message = EnumMessage.getDefaultInstance()
      testMessageBasics(message)
    }

    it("Simple map message to schema") {
      val message = SimpleMapMessage.getDefaultInstance()
      testMessageBasics(message)
    }

    it("Nested map message to schema") {
      val message = NestedMapMessage.getDefaultInstance()
      testMessageBasics(message)
    }
  }
})

/**
 * Tests the basics for any message and returns the map for further processing
 */
fun testMessageBasics(message: GeneratedMessageV3): Map<KType, JsonSchema> {
  val expectedType: KType = message::class.createType()
  // Results after conversion
  val resultSchema: Map<KType, JsonSchema> = message.createCustomTypesForTypeAndSubTypes()
  val resultEntry: JsonSchema = resultSchema.values.first()

  resultSchema.keys.first().shouldBe(expectedType)
  when (resultEntry) {
    // Should have all our enum entries
    is EnumDefinition -> resultEntry.enum.size.shouldBe(message.descriptorForType.enumTypes.size)
    // should contain all our fields
    is TypeDefinition -> resultEntry.properties?.size.shouldBe(message.descriptorForType.fields.size)
    else -> {}
  }
  return resultSchema
}
