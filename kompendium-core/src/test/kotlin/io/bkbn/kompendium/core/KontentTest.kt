package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.Kontent.generateKontent
import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.TestBigNumberModel
import io.bkbn.kompendium.core.fixtures.TestByteArrayModel
import io.bkbn.kompendium.core.fixtures.TestInvalidMap
import io.bkbn.kompendium.core.fixtures.TestNestedModel
import io.bkbn.kompendium.core.fixtures.TestSimpleModel
import io.bkbn.kompendium.core.fixtures.TestSimpleWithEnumList
import io.bkbn.kompendium.core.fixtures.TestSimpleWithEnums
import io.bkbn.kompendium.core.fixtures.TestSimpleWithList
import io.bkbn.kompendium.core.fixtures.TestSimpleWithMap
import io.bkbn.kompendium.core.fixtures.TestWithUUID
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class KontentTest : DescribeSpec({
  describe("Kontent analysis") {
    it("Can return an empty map when passed Unit") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<Unit>(cache)

      // assert
      cache should beEmpty()
    }
    it("Can return a single map result when analyzing a primitive") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<Long>(cache)

      // assert
      cache shouldHaveSize 1
      cache["Long"] shouldBe FormattedSchema("int64", "integer")
    }
    it("Can handle BigDecimal and BigInteger Types") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestBigNumberModel>(cache)

      // assert
      cache shouldHaveSize 3
      cache shouldContainKey TestBigNumberModel::class.simpleName!!
      cache["BigDecimal"] shouldBe FormattedSchema("double", "number")
      cache["BigInteger"] shouldBe FormattedSchema("int64", "integer")
    }
    it("Can handle ByteArray type") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestByteArrayModel>(cache)

      // assert
      cache shouldHaveSize 2
      cache shouldContainKey TestByteArrayModel::class.simpleName!!
      cache["ByteArray"] shouldBe FormattedSchema("byte", "string")
    }
    it("Allows objects to reference their base type in the cache") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestSimpleModel>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 3
      cache shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("Can generate cache for nested object types") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestNestedModel>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 4
      cache shouldContainKey TestNestedModel::class.simpleName!!
      cache shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("Does not repeat generation for cached items") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // arrange
      val clazz = TestNestedModel::class
      generateKontent<TestNestedModel>(cache)

      // act
      generateKontent<TestSimpleModel>(cache)

      // assert TODO Spy to check invocation count?
      cache shouldNotBe null
      cache shouldHaveSize 4
      cache shouldContainKey clazz.simpleName!!
      cache shouldContainKey TestSimpleModel::class.simpleName!!
    }
    it("allows for generation of enum types") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestSimpleWithEnums>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 3
      cache shouldContainKey TestSimpleWithEnums::class.simpleName!!
    }
    it("Allows for generation of map fields") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestSimpleWithMap>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 5
      cache shouldContainKey "Map-String-TestSimpleModel"
      cache shouldContainKey TestSimpleWithMap::class.simpleName!!
      cache[TestSimpleWithMap::class.simpleName] as ObjectSchema shouldNotBe null // TODO Improve
    }
    it("Throws an error if a map is of an invalid type") {
      // assert
      shouldThrow<IllegalStateException> { generateKontent<TestInvalidMap>() }
    }
    it("Can generate for collection fields") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestSimpleWithList>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 6
      cache shouldContainKey "List-TestSimpleModel"
      cache shouldContainKey TestSimpleWithList::class.simpleName!!
    }
    it("Can parse an enum list as a field") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestSimpleWithEnumList>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 4
      cache shouldHaveKey "List-SimpleEnum"
    }
    it("Can support UUIDs") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<TestWithUUID>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 2
      cache shouldContainKey UUID::class.simpleName!!
      cache shouldContainKey TestWithUUID::class.simpleName!!
      cache[UUID::class.simpleName] as FormattedSchema shouldBe FormattedSchema("uuid", "string")
    }
    it("Can generate a top level list response") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<List<TestSimpleModel>>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 4
      cache shouldContainKey "List-TestSimpleModel"
    }
    it("Can handle a complex type") {
      // arrange
      val cache: SchemaMap = mutableMapOf()

      // act
      generateKontent<ComplexRequest>(cache)

      // assert
      cache shouldNotBe null
      cache shouldHaveSize 7
      cache shouldContainKey "Map-String-CrazyItem"
      cache["Map-String-CrazyItem"] as DictionarySchema shouldNotBe null
    }
  }
})
