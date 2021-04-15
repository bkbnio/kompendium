package org.leafygreens.kompendium.util

import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumRequest
import org.leafygreens.kompendium.annotations.KompendiumResponse

data class TestSimpleModel(val a: String, val b: Int)

data class TestNestedModel(val inner: TestSimpleModel)

data class TestSimpleWithEnums(val a: String, val b: SimpleEnum)

data class TestParams(val a: String, val aa: Int)

data class TestNested(val nesty: String)

@KompendiumRequest("Example Request")
data class TestRequest(
  @KompendiumField(name = "field_name")
  val fieldName: TestNested,
  val b: Double,
  val aaa: List<Long>
)

@KompendiumResponse(KompendiumHttpCodes.OK, "A Successful Endeavor")
data class TestResponse(val c: String)

@KompendiumResponse(KompendiumHttpCodes.CREATED, "Created Successfully")
data class TestCreatedResponse(val id: Int, val c: String)

@KompendiumResponse(KompendiumHttpCodes.NO_CONTENT, "Entity was deleted successfully")
object TestDeleteResponse

@KompendiumRequest("Request object to create a backbone project")
data class ComplexRequest(
  val org: String,
  @KompendiumField("amazing_field")
  val amazingField: String,
  val tables: List<NestedComplexItem>
) {
  fun testThing() {
    println("hey mom 👋")
  }
}

data class NestedComplexItem(
  val name: String,
  val alias: CustomAlias
)

typealias CustomAlias = Map<String, CrazyItem>

data class CrazyItem(val enumeration: SimpleEnum)

enum class SimpleEnum {
  ONE,
  TWO
}

sealed class TestSealedClass(open val a: String)

data class SimpleTSC(val b: Int) : TestSealedClass("hey")
open class MediumTSC(override val a: String, val b: Int) : TestSealedClass(a)
data class WildTSC(val c: Boolean, val d: String, val e: Int) : MediumTSC(d, e)
