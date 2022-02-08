package io.bkbn.kompendium.core.fixtures

import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.FreeFormObject
import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.annotations.Referenced
import io.bkbn.kompendium.annotations.UndeclaredField
import io.bkbn.kompendium.annotations.constraint.Format
import io.bkbn.kompendium.annotations.constraint.MaxItems
import io.bkbn.kompendium.annotations.constraint.MaxLength
import io.bkbn.kompendium.annotations.constraint.MaxProperties
import io.bkbn.kompendium.annotations.constraint.Maximum
import io.bkbn.kompendium.annotations.constraint.MinItems
import io.bkbn.kompendium.annotations.constraint.MinLength
import io.bkbn.kompendium.annotations.constraint.MinProperties
import io.bkbn.kompendium.annotations.constraint.Minimum
import io.bkbn.kompendium.annotations.constraint.MultipleOf
import io.bkbn.kompendium.annotations.constraint.Pattern
import io.bkbn.kompendium.annotations.constraint.UniqueItems
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

data class TestSimpleModel(val a: String, val b: Int)

data class TestBigNumberModel(val a: BigDecimal, val b: BigInteger)

data class TestByteArrayModel(val a: ByteArray)

data class TestNestedModel(val inner: TestSimpleModel)

data class TestSimpleWithEnums(val a: String, val b: SimpleEnum)

data class TestSimpleWithMap(val a: String, val b: Map<String, TestSimpleModel>)

data class TestSimpleWithList(val a: Boolean, val b: List<TestSimpleModel>)

data class TestSimpleWithEnumList(val a: Double, val b: List<SimpleEnum>)

data class TestInvalidMap(val a: Map<Int, TestSimpleModel>)

data class TestParams(
  @Param(ParamType.PATH) val a: String,
  @Param(ParamType.QUERY) val aa: Int
)

@Serializable
data class TestNested(val nesty: String)

data class TestWithUUID(val id: UUID)

@Serializable
data class TestRequest(
  @Field(name = "field_name")
  val fieldName: TestNested,
  val b: Double,
  val aaa: List<Long>
)

@Serializable
data class TestResponse(val c: String)

data class TestGeneric<T>(val messy: String, val potato: T)

data class TestCreatedResponse(val id: Int, val c: String)

data class TestFieldOverride(
  @Field(name = "real_name", description = "A Field that is super important!")
  val b: Boolean
)

data class MinMaxInt(
  @Minimum("5")
  @Maximum("100")
  val a: Int
)

data class MinMaxDouble(
  @Minimum("5.5")
  @Maximum("13.37")
  val a: Double
)

data class ExclusiveMinMax(
  @Minimum("5", true)
  @Maximum("100", true)
  val a: Int
)

data class FormattedString(
  @Format("password")
  @Param(ParamType.QUERY)
  val a: String
)

data class MinMaxString(
  @MinLength(42)
  @MaxLength(1337)
  val a: String
)

data class RegexString(
  @Pattern("^\\d{3}-\\d{2}-\\d{4}\$")
  val a: String
)

data class MinMaxArray(
  @MinItems(1)
  @MaxItems(10)
  val a: List<String>
)

data class UniqueArray(
  @UniqueItems
  val a: List<Int>
)

data class MultipleOfInt(
  @MultipleOf("5")
  val a: Int
)

data class MultipleOfDouble(
  @MultipleOf("2.5")
  val a: Double
)

data class FreeFormData(
  @FreeFormObject
  val data: JsonElement
)

data class MinMaxFreeForm(
  @FreeFormObject
  @MinProperties(5)
  @MaxProperties(10)
  val data: JsonElement
)


data class RequiredParam(
  @Param(ParamType.QUERY)
  val a: String
)

data class DefaultParam(
  @Param(ParamType.QUERY)
  val b: String = "heyo"
)

data class DefaultField(
  val a: String = "hi",
  val b: Int
)

data class NullableField(
  val a: String?
)

data class ComplexRequest(
  val org: String,
  @Field("amazing_field")
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
  @Param(ParamType.QUERY) val a: Int = 100,
  @Param(ParamType.PATH) val b: String?,
  @Param(ParamType.PATH) val c: Boolean
)

data class ExceptionResponse(val message: String)

data class OptionalParams(
  @Param(ParamType.QUERY) val required: String,
  @Param(ParamType.QUERY) val notRequired: String?
)

sealed class FlibbityGibbit

data class SimpleGibbit(val a: String) : FlibbityGibbit()
data class ComplexGibbit(val b: String, val c: Int) : FlibbityGibbit()

sealed interface SlammaJamma

data class OneJamma(val a: Int) : SlammaJamma
data class AnothaJamma(val b: Float) : SlammaJamma

@Referenced
data class InsaneJamma(val c: SlammaJamma) : SlammaJamma

sealed interface Flibbity<T>

data class Gibbity<T>(val a: T) : Flibbity<T>
data class Bibbity<T>(val b: String, val f: T) : Flibbity<T>

enum class Hehe {
  HAHA,
  HOHO
}

@UndeclaredField("nowYouDont", Hehe::class)
data class Mysterious(val nowYouSeeMe: String)

data class HeaderNameTest(
  @Param(type = ParamType.HEADER) val `X-UserEmail`: String
)

enum class ColumnMode {
  NULLABLE,
  REQUIRED,
  REPEATED
}

@Referenced
data class ColumnSchema(
  val name: String,
  val type: String,
  val description: String,
  val mode: ColumnMode,
  val subColumns: List<ColumnSchema> = emptyList()
)
