package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.genericPolymorphicResponse
import io.bkbn.kompendium.core.util.genericPolymorphicResponseMultipleImpls
import io.bkbn.kompendium.core.util.gnarlyGenericResponse
import io.bkbn.kompendium.core.util.nestedGenericCollection
import io.bkbn.kompendium.core.util.nestedGenericMultipleParamsCollection
import io.bkbn.kompendium.core.util.nestedGenericResponse
import io.bkbn.kompendium.core.util.overrideSealedTypeIdentifier
import io.bkbn.kompendium.core.util.polymorphicCollectionResponse
import io.bkbn.kompendium.core.util.polymorphicMapResponse
import io.bkbn.kompendium.core.util.polymorphicResponse
import io.bkbn.kompendium.core.util.simpleGenericResponse
import io.bkbn.kompendium.core.util.subtypeNotCompleteSetOfParentProperties
import io.kotest.core.spec.style.DescribeSpec

class KompendiumPolymorphismAndGenericsTest : DescribeSpec({
  describe("Polymorphism and Generics") {
    it("can generate a polymorphic response type") {
      TestHelpers.openApiTestAllSerializers("T0027__polymorphic_response.json") { polymorphicResponse() }
    }
    it("Can generate a collection with polymorphic response type") {
      TestHelpers.openApiTestAllSerializers("T0028__polymorphic_list_response.json") { polymorphicCollectionResponse() }
    }
    it("Can generate a map with a polymorphic response type") {
      TestHelpers.openApiTestAllSerializers("T0029__polymorphic_map_response.json") { polymorphicMapResponse() }
    }
    it("Can generate a response type with a generic type") {
      TestHelpers.openApiTestAllSerializers("T0030__simple_generic_response.json") { simpleGenericResponse() }
    }
    it("Can generate a response type with a nested generic type") {
      TestHelpers.openApiTestAllSerializers("T0031__nested_generic_response.json") { nestedGenericResponse() }
    }
    it("Can generate a polymorphic response type with generics") {
      TestHelpers.openApiTestAllSerializers(
        "T0032__polymorphic_response_with_generics.json"
      ) { genericPolymorphicResponse() }
    }
    it("Can handle an absolutely psycho inheritance test") {
      TestHelpers.openApiTestAllSerializers("T0033__crazy_polymorphic_example.json") {
        genericPolymorphicResponseMultipleImpls()
      }
    }
    it("Can support nested generic collections") {
      TestHelpers.openApiTestAllSerializers("T0039__nested_generic_collection.json") { nestedGenericCollection() }
    }
    it("Can support nested generics with multiple type parameters") {
      TestHelpers.openApiTestAllSerializers("T0040__nested_generic_multiple_type_params.json") {
        nestedGenericMultipleParamsCollection()
      }
    }
    it("Can handle a really gnarly generic example") {
      TestHelpers.openApiTestAllSerializers("T0043__gnarly_generic_example.json") { gnarlyGenericResponse() }
    }
    it("Can override the type name for a sealed interface implementation") {
      TestHelpers.openApiTestAllSerializers("T0070__sealed_interface_type_name_override.json") {
        overrideSealedTypeIdentifier()
      }
    }
    it("Can serialize an object where the subtype is not a complete set of parent properties") {
      TestHelpers.openApiTestAllSerializers("T0071__subtype_not_complete_set_of_parent_properties.json") {
        subtypeNotCompleteSetOfParentProperties()
      }
    }
  }
})
