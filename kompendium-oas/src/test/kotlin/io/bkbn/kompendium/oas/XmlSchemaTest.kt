package io.bkbn.kompendium.oas

import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

private val encoder = Json {
  prettyPrint = true
  encodeDefaults = true
  explicitNulls = false
}

class XmlSchemaTest : DescribeSpec({
    describe("XmlSchema") {
      it("name replacement") {
        val animals = SimpleSchema("string", xml = SimpleSchema.Xml(name = "animal"))

        val json = encode("animals" to animals)
        json shouldBe """
{
    "schemas": {
        "animals": {
            "type": "string",
            "xml": {
                "name": "animal"
            }
        }
    },
    "securitySchemes": {
    }
}""".trimIndent()
      }

      it("attribute, prefix, and namespace") {
        val person = ObjectSchema(
          properties = mapOf(
            "id" to SimpleSchema("integer", format = "int32", xml = SimpleSchema.Xml(attribute = true)),
            "name" to SimpleSchema("string", xml = SimpleSchema.Xml(namespace = "http://example.com/schema/sample", prefix = "sample"))
          )
        )
        val json = encode("Person" to person)
        json shouldBe """
{
    "schemas": {
        "Person": {
            "properties": {
                "id": {
                    "type": "integer",
                    "format": "int32",
                    "xml": {
                        "attribute": true
                    }
                },
                "name": {
                    "type": "string",
                    "xml": {
                        "namespace": "http://example.com/schema/sample",
                        "prefix": "sample"
                    }
                }
            },
            "type": "object"
        }
    },
    "securitySchemes": {
    }
}
        """.trimIndent()
      }

      describe("arrays") {
        it("can change element names") {
          val animals = ArraySchema(
            items = SimpleSchema("string", xml = SimpleSchema.Xml(name = "animal"))
          )

          val json = encode("animals" to animals)
          json shouldBe """
{
    "schemas": {
        "animals": {
            "items": {
                "type": "string",
                "xml": {
                    "name": "animal"
                }
            },
            "type": "array"
        }
    },
    "securitySchemes": {
    }
}
        """.trimIndent()
        }

        it("can name array and items") {
          val animals = ArraySchema(
            items = SimpleSchema("string", xml = SimpleSchema.Xml(name = "animal")),
            xml = ArraySchema.Xml(name = "aliens")
          )

          val json = encode("animals" to animals)
          json shouldBe """
{
    "schemas": {
        "animals": {
            "items": {
                "type": "string",
                "xml": {
                    "name": "animal"
                }
            },
            "xml": {
                "name": "aliens"
            },
            "type": "array"
        }
    },
    "securitySchemes": {
    }
}
          """.trimIndent()
        }

        it("can mark arrays as wrapped") {
          val animals = ArraySchema(
            items = SimpleSchema("string", xml = SimpleSchema.Xml(name = "animal")),
            xml = ArraySchema.Xml(wrapped = true)
          )

          val json = encode("animals" to animals)
          json shouldBe """
{
    "schemas": {
        "animals": {
            "items": {
                "type": "string",
                "xml": {
                    "name": "animal"
                }
            },
            "xml": {
                "wrapped": true
            },
            "type": "array"
        }
    },
    "securitySchemes": {
    }
}
          """.trimIndent()

        }

        it("can wrap and name array and items") {
          val animals = ArraySchema(
            items = SimpleSchema("string", xml = SimpleSchema.Xml(name = "animal")),
            xml = ArraySchema.Xml(name = "aliens", wrapped = true)
          )

          val json = encode("animals" to animals)
          json shouldBe """
{
    "schemas": {
        "animals": {
            "items": {
                "type": "string",
                "xml": {
                    "name": "animal"
                }
            },
            "xml": {
                "name": "aliens",
                "wrapped": true
            },
            "type": "array"
        }
    },
    "securitySchemes": {
    }
}
          """.trimIndent()
        }

      }
    }
})

private fun encode(schemaPair: Pair<String, ComponentSchema>): String {
  val components = Components(
    schemas = mutableMapOf(
      schemaPair
    )
  )

  return encoder.encodeToString(Components.serializer(), components)
}
