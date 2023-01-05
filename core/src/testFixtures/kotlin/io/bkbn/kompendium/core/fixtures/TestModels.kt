package io.bkbn.kompendium.core.fixtures

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.Instant

@Serializable
data class TestNested(val nesty: String)

@Serializable
data class DoubleResponse(val payload: Double)

@Serializable
data class TestRequest(
  val fieldName: TestNested,
  val b: Double,
  val aaa: List<Long>
)

@Serializable
data class TestSimpleRequest(
  val a: String,
  val b: Int
)

@Serializable
data class TestResponse(val c: String)

@Serializable
enum class TestEnum {
  YES,
  NO
}

@Serializable
data class NullableEnum(val a: TestEnum? = null)

data class TestCreatedResponse(val id: Int, val c: String)

data class DateTimeString(
  val a: Instant
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

data class ExceptionResponse(val message: String)

sealed class FlibbityGibbit {
  abstract val z: String
}

data class SimpleGibbit(val a: String, override val z: String = "z") : FlibbityGibbit()
data class ComplexGibbit(val b: String, val c: Int, override val z: String) : FlibbityGibbit()

sealed interface SlammaJamma

data class OneJamma(val a: Int) : SlammaJamma
data class AnothaJamma(val b: Float) : SlammaJamma

data class InsaneJamma(val c: SlammaJamma) : SlammaJamma

sealed interface Flibbity<T>

data class Gibbity<T>(val a: T) : Flibbity<T>
data class Bibbity<T>(val b: String, val f: T) : Flibbity<T>

data class NestedFlibbity<T>(
  val flibbity: Flibbity<T>
)

enum class ColumnMode {
  NULLABLE,
  REQUIRED,
  REPEATED
}

data class ColumnSchema(
  val name: String,
  val type: String,
  val description: String,
  val mode: ColumnMode,
  val subColumns: List<ColumnSchema> = emptyList()
)

@Serializable
public data class ProfileUpdateRequest(
  public val mood: String?,
  public val viewCount: Long?,
  public val metadata: ProfileMetadataUpdateRequest?
)


@Serializable
public data class ProfileMetadataUpdateRequest(
  public val isPrivate: Boolean?,
  public val otherThing: String?
)

data class Page<T>(
  val content: List<T>,
  val totalElements: Long,
  val totalPages: Int,
  val numberOfElements: Int,
  val number: Int,
  val size: Int
)

data class MultiNestedGenerics<T, E>(
  val content: Map<T, E>
)

data class Something(val a: String, val b: Int)

data class ManyThings(
  val someA: Something,
  val someB: Something?
)

data class Foosy<T, K>(val test: T, val otherThing: List<K>)
data class Barzo<G>(val result: G)

object Nested {
  @Serializable
  data class Response(val idk: Boolean)
}

@Serializable
data class TransientObject(
  @field:Expose
  val nonTransient: String,
  @field:JsonIgnore
  @Transient
  val transient: String = "transient"
)

@Serializable
data class UnbackedObject(
  val backed: String
) {
  val unbacked: String get() = "unbacked"
}

@Serializable
data class SerialNameObject(
  @field:JsonProperty("snake_case_name")
  @field:SerializedName("snake_case_name")
  @SerialName("snake_case_name")
  val camelCaseName: String
)

enum class Color {
  RED,
  GREEN,
  BLUE
}

@Serializable
data class ObjectWithEnum(
  val color: Color
)
