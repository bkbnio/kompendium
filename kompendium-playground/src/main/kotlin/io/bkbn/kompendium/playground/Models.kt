package io.bkbn.kompendium.playground

import io.bkbn.kompendium.annotations.KompendiumField
import io.bkbn.kompendium.annotations.KompendiumParam
import io.bkbn.kompendium.annotations.ParamType
import io.bkbn.kompendium.annotations.UndeclaredField
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

data class ExampleParams(
  @KompendiumParam(ParamType.PATH) val id: Int,
  @KompendiumParam(ParamType.QUERY) val name: String
)

data class JustQuery(
  @KompendiumParam(ParamType.QUERY) val potato: Boolean,
  @KompendiumParam(ParamType.QUERY) val tomato: String
)

data class ExampleNested(val nesty: String)

data class ExampleGeneric<T>(val potato: T)

data class ExampleRequest(
  @KompendiumField(name = "field_name")
  val fieldName: ExampleNested,
  val b: Double,
  val aaa: List<Long>
)

@Serializable
data class ExampleResponse(val c: String)

data class ExceptionResponse(val message: String)

data class ExampleCreatedResponse(val id: Int, val c: String)

data class DateTimeWrapper(val dt: DateTime)

enum class Testerino {
  First,
  Second
}

@UndeclaredField("type", Testerino::class)
data class SimpleYetMysterious(val exists: Boolean)
