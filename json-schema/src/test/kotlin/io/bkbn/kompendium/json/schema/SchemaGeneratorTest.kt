package io.bkbn.kompendium.json.schema

import io.bkbn.kompendium.core.fixtures.AnnotatedTestRequest
import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.FlibbityGibbit
import io.bkbn.kompendium.core.fixtures.NestedComplexItem
import io.bkbn.kompendium.core.fixtures.ObjectWithEnum
import io.bkbn.kompendium.core.fixtures.SerialNameObject
import io.bkbn.kompendium.core.fixtures.SimpleEnum
import io.bkbn.kompendium.core.fixtures.SlammaJamma
import io.bkbn.kompendium.core.fixtures.TestResponse
import io.bkbn.kompendium.core.fixtures.TestSimpleRequest
import io.bkbn.kompendium.core.fixtures.TransientObject
import io.bkbn.kompendium.core.fixtures.UnbackedObject
import io.bkbn.kompendium.core.fixtures.GenericObject
import io.bkbn.kompendium.core.fixtures.TestHelpers.getFileSnapshot
import io.bkbn.kompendium.enrichment.CollectionEnrichment
import io.bkbn.kompendium.enrichment.NumberEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.StringEnrichment
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.reflect.typeOf

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
    it("Can generate the schema for a UUID") {
      jsonSchemaTest<UUID>("T0017__scalar_uuid.json")
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
      // Same schema as a non-nullable type, since the nullability will be handled on the property
      jsonSchemaTest<TestSimpleRequest?>("T0006__nullable_object.json")
    }
    it("Can generate the schema for a polymorphic object") {
      jsonSchemaTest<FlibbityGibbit>("T0015__polymorphic_object.json")
    }
    it("Can generate the schema for a recursive type") {
      jsonSchemaTest<SlammaJamma>("T0016__recursive_object.json")
    }
    it("Can generate the schema for object with transient property") {
      jsonSchemaTest<TransientObject>("T0018__transient_object.json")
    }
    it("Can generate the schema for object with unbacked property") {
      jsonSchemaTest<UnbackedObject>("T0019__unbacked_object.json")
    }
    it("Can generate the schema for object with SerialName annotation") {
      jsonSchemaTest<SerialNameObject>("T0020__serial_name_object.json")
    }
    it("Can generate the schema for object with generic property") {
      jsonSchemaTest<GenericObject<TestSimpleRequest>>("T0024__generic_object.json")
    }
  }
  describe("Enums") {
    it("Can generate the schema for a simple enum") {
      jsonSchemaTest<SimpleEnum>("T0007__simple_enum.json")
    }
    it("Can generate the schema for a nullable enum") {
      // Same schema as a non-nullable enum, since the nullability will be handled on the property
      jsonSchemaTest<SimpleEnum?>("T0008__nullable_enum.json")
    }
    it("Can generate the schema for an object with an enum property") {
      jsonSchemaTest<ObjectWithEnum>("T0021__object_with_enum.json")
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
      shouldThrow<IllegalArgumentException> {
        SchemaGenerator.fromTypeToSchema(
          typeOf<Map<Int, Int>>(),
          cache = mutableMapOf(),
          schemaConfigurator = KotlinXSchemaConfigurator()
        )
      }
    }
    it("Can generate the schema for a map of objects") {
      jsonSchemaTest<Map<String, TestResponse>>("T0013__object_map.json")
    }
    it("Can generate the schema for a nullable map") {
      jsonSchemaTest<Map<String, Int>?>("T0014__nullable_map.json")
    }
  }
  describe("Enrichment") {
    it("Can attach an enrichment to a simple type") {
      jsonSchemaTest<TestSimpleRequest>(
        snapshotName = "T0022__enriched_simple_object.json",
        enrichment = ObjectEnrichment("simple") {
          TestSimpleRequest::a {
            StringEnrichment("blah") {
              description = "This is a simple description"
            }
          }
          TestSimpleRequest::b {
            NumberEnrichment("bla") {
              deprecated = true
            }
          }
        }
      )
    }
    it("Can attach an enrichment taken from an annotations") {
      jsonSchemaTest<AnnotatedTestRequest>(snapshotName = "T0026__enriched_annotated_object.json")
    }
    it("Can properly assign a reference to a nested enrichment") {
      jsonSchemaTest<ComplexRequest>(
        snapshotName = "T0023__enriched_nested_reference.json",
        enrichment = ObjectEnrichment("example") {
          ComplexRequest::tables {
            CollectionEnrichment<List<NestedComplexItem>>("tables") {
              description = "Collection of important items"
              itemEnrichment = ObjectEnrichment("table") {
                NestedComplexItem::name {
                  StringEnrichment("name") {
                    description = "The name of the table"
                  }
                }
              }
            }
          }
        }
      )
    }
    it("Can properly assign a reference to a generic object") {
      jsonSchemaTest<GenericObject<TestSimpleRequest>>(
        snapshotName = "T0025__enrichment_generic_object.json",
        enrichment = ObjectEnrichment("generic") {
          GenericObject<TestSimpleRequest>::data {
            ObjectEnrichment<TestSimpleRequest>("blob") {
              description = "This is a generic object"
              TestSimpleRequest::a {
                StringEnrichment("blah") {
                  description = "This is a simple description"
                }
              }
              TestSimpleRequest::b {
                NumberEnrichment("bla") {
                  deprecated = true
                }
              }
            }
          }
        }
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

    private fun JsonSchema.serialize() = json.encodeToString(JsonSchema.serializer(), this)

    private inline fun <reified T> jsonSchemaTest(snapshotName: String, enrichment: ObjectEnrichment<*>? = null) {
      // act
      val schema = SchemaGenerator.fromTypeToSchema(
        type = typeOf<T>(),
        cache = mutableMapOf(),
        schemaConfigurator = KotlinXSchemaConfigurator(),
        enrichment = enrichment,
      )

      // assert
      schema.serialize() shouldEqualJson getFileSnapshot(snapshotName)
    }
  }
}
