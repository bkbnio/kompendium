package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.Kontent.generateKontent
import io.bkbn.kompendium.core.Kontent.generateParameterKontent
import io.bkbn.kompendium.core.util.ComplexRequest
import io.bkbn.kompendium.core.util.TestBigNumberModel
import io.bkbn.kompendium.core.util.TestByteArrayModel
import io.bkbn.kompendium.core.util.TestInvalidMap
import io.bkbn.kompendium.core.util.TestNestedModel
import io.bkbn.kompendium.core.util.TestSimpleModel
import io.bkbn.kompendium.core.util.TestSimpleWithEnumList
import io.bkbn.kompendium.core.util.TestSimpleWithEnums
import io.bkbn.kompendium.core.util.TestSimpleWithList
import io.bkbn.kompendium.core.util.TestSimpleWithMap
import io.bkbn.kompendium.core.util.TestWithUUID
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotHaveKey
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class KontentTest : DescribeSpec({
  describe("Kontent analysis") {
    it("Can return an empty map when passed Unit") {
      // act
      val result = generateKontent<Unit>()

      // assert
      result should beEmpty()
    }
    it("Can return a single map result when analyzing a primitive") {
      // act
      val result = generateKontent<Long>()

      // assert
      result shouldHaveSize 1
      result["Long"] shouldBe FormattedSchema("int64", "integer")
    }
    it("Can handle BigDecimal and BigInteger Types") {
      // act
      val result = generateKontent<TestBigNumberModel>()

      // assert
      result shouldHaveSize 3
      result shouldContainKey TestBigNumberModel::class.simpleName!!
      result["BigDecimal"] shouldBe FormattedSchema("double", "number")
      result["BigInteger"] shouldBe FormattedSchema("int64", "integer")
    }
    it("Can handle ByteArray type") {
      // act
      val result = generateKontent<TestByteArrayModel>()

      // assert
      result shouldHaveSize 2
      result shouldContainKey TestByteArrayModel::class.simpleName!!
      result["ByteArray"] shouldBe FormattedSchema("byte", "string")
    }
    it("Allows objects to reference their base type in the cache") {
      // act
      val result = generateKontent<TestSimpleModel>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 3
      result shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("Can generate cache for nested object types") {
      // act
      val result = generateKontent<TestNestedModel>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 4
      result shouldContainKey TestNestedModel::class.simpleName!!
      result shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("Does not repeat generation for cached items") {
      // arrange
      val clazz = TestNestedModel::class
      val initialCache = generateKontent<TestNestedModel>()

      // act
      val result = generateKontent<TestSimpleModel>(initialCache)

      // assert TODO Spy to check invocation count?
      result shouldNotBe null
      result shouldHaveSize 4
      result shouldContainKey clazz.simpleName!!
      result shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("allows for generation of enum types") {
      // act
      val result = generateKontent<TestSimpleWithEnums>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 3
      result shouldContainKey TestSimpleWithEnums::class.simpleName!!
    }
    it("Allows for generation of map fields") {
      // act
      val result = generateKontent<TestSimpleWithMap>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 5
      result shouldContainKey "Map-String-TestSimpleModel"
      result shouldContainKey TestSimpleWithMap::class.simpleName!!
      result[TestSimpleWithMap::class.simpleName] as ObjectSchema shouldNotBe null // TODO Improve
    }
    it("Throws an error if a map is of an invalid type") {
      // assert
      shouldThrow<IllegalStateException> { generateKontent<TestInvalidMap>() }
    }
    it("Can generate for collection fields") {
      // act
      val result = generateKontent<TestSimpleWithList>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 6
      result shouldContainKey "List-TestSimpleModel"
      result shouldContainKey TestSimpleWithList::class.simpleName!!
    }
    it("Can parse an enum list as a field") {
      // act
      val result = generateKontent<TestSimpleWithEnumList>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 4
      result shouldHaveKey "List-SimpleEnum"
    }
    it("Can support UUIDs") {
      // act
      val result = generateKontent<TestWithUUID>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 2
      result shouldContainKey UUID::class.simpleName!!
      result shouldContainKey TestWithUUID::class.simpleName!!
      result[UUID::class.simpleName] as FormattedSchema shouldBe FormattedSchema("uuid", "string")
    }
    it("Can generate a top level list response") {
      // act
      val result = generateKontent<List<TestSimpleModel>>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 4
      result shouldContainKey "List-TestSimpleModel"
    }
    it("Can handle a complex type") {
      // act
      val result = generateKontent<ComplexRequest>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 7
      result shouldContainKey "Map-String-CrazyItem"
      result["Map-String-CrazyItem"] as DictionarySchema shouldNotBe null
    }
    it("Filters out top level declaration from parameter kontent") {
      // act
      val result = generateParameterKontent<TestSimpleModel>()

      // assert
      result shouldNotBe null
      result shouldHaveSize 2
      result shouldNotHaveKey TestSimpleModel::class.simpleName!!
    }
  }
})
