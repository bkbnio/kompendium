package org.leafygreens.kompendium.util

import org.leafygreens.kompendium.annotations.KompendiumField
import org.leafygreens.kompendium.annotations.KompendiumRequest
import org.leafygreens.kompendium.annotations.KompendiumResponse

data class TestParams(val a: String, val aa: Int)

data class TestNested(val nesty: String)

@KompendiumRequest("Example Request")
data class TestRequest(
  @KompendiumField(name = "field_name")
  val fieldName: TestNested,
  val b: Double,
  val aaa: List<Long>
)

@KompendiumResponse(200, "A Successful Endeavor")
data class TestResponse(val c: String)

@KompendiumResponse(201, "Created Successfully")
data class TestCreatedResponse(val id: Int, val c: String)

@KompendiumResponse(status = 204, "Entity was deleted successfully")
object TestDeleteResponse
