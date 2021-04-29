package org.leafygreens.kompendium.util

import java.util.UUID
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.PathParam
import org.leafygreens.kompendium.annotations.QueryParam

data class TestSimpleModel(val a: String, val b: Int)

data class TestNestedModel(val inner: TestSimpleModel)

data class TestSimpleWithEnums(val a: String, val b: SimpleEnum)

data class TestSimpleWithMap(val a: String, val b: Map<String, TestSimpleModel>)

data class TestSimpleWithList(val a: Boolean, val b: List<TestSimpleModel>)

data class TestSimpleWithEnumList(val a: Double, val b: List<SimpleEnum>)

data class TestInvalidMap(val a: Map<Int, TestSimpleModel>)

data class TestParams(
  @PathParam val a: String,
  @QueryParam val aa: Int
)

data class TestNested(val nesty: String)

data class TestWithUUID(val id: UUID)

data class TestRequest(
  @KompendiumField(name = "field_name")
  val fieldName: TestNested,
  val b: Double,
  val aaa: List<Long>
)

data class TestResponse(val c: String)

data class TestCreatedResponse(val id: Int, val c: String)

object TestDeleteResponse

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

data class ExceptionResponse(val message: String)
