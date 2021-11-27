package io.bkbn.kompendium.auth.util

import io.bkbn.kompendium.core.Kompendium
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.ktor.http.HttpStatusCode
import java.io.File

object TestData {
  const val OPEN_API_ENDPOINT = "/openapi.json"
  object AuthConfigName {
    const val Basic = "basic"
    const val JWT = "jwt"
  }

  const val getRoutePath = "/test"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }

  private val testGetResponse = ResponseInfo<TestResponse>(HttpStatusCode.OK, "A Successful Endeavor")

  fun testGetInfo(vararg security: String) =
    MethodInfo.GetInfo<TestParams, TestResponse>(
      summary = "Another get test",
      description = "testing more",
      responseInfo = testGetResponse,
      securitySchemes = security.toSet()
    )
}
