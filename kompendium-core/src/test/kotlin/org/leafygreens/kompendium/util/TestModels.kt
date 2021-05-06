package org.leafygreens.kompendium.util

import java.util.UUID
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import org.leafygreens.kompendium.MethodParser
import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumParam
import org.leafygreens.kompendium.annotations.ParamType

data class TestSimpleModel(val a: String, val b: Int)

data class TestNestedModel(val inner: TestSimpleModel)

data class TestSimpleWithEnums(val a: String, val b: SimpleEnum)

data class TestSimpleWithMap(val a: String, val b: Map<String, TestSimpleModel>)

data class TestSimpleWithList(val a: Boolean, val b: List<TestSimpleModel>)

data class TestSimpleWithEnumList(val a: Double, val b: List<SimpleEnum>)

data class TestInvalidMap(val a: Map<Int, TestSimpleModel>)

data class TestParams(
  @KompendiumParam(ParamType.PATH) val a: String,
  @KompendiumParam(ParamType.QUERY) val aa: Int
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

data class ComplexRequest(
  val org: String,
  @KompendiumField("amazing_field")
  val amazingField: String,
  val tables: List<NestedComplexItem>
)

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

data class DefaultParameter(
  @KompendiumParam(ParamType.QUERY) val a: Int = 100,
  @KompendiumParam(ParamType.PATH) val b: String?,
  @KompendiumParam(ParamType.PATH) val c: Boolean
)

data class ExceptionResponse(val message: String)

data class OptionalParams(
  @KompendiumParam(ParamType.QUERY) val required: String,
  @KompendiumParam(ParamType.QUERY) val notRequired: String?
)
