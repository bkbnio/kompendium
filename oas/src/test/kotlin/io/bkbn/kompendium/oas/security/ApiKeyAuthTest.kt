package io.bkbn.kompendium.oas.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

private val json = Json { encodeDefaults = true }

class ApiKeyAuthTest : DescribeSpec({
  describe("ApiKeyAuth") {
    it("should produce correct json") {
      mapOf(
        ApiKeyAuth.ApiKeyLocation.HEADER to "header",
        ApiKeyAuth.ApiKeyLocation.COOKIE to "cookie",
        ApiKeyAuth.ApiKeyLocation.QUERY to "query",
      ).forEach {
        val example = ApiKeyAuth(it.key, "test-name")

        val json = json.encodeToString(ApiKeyAuth.serializer(), example)

        json.shouldBe("""{"in":"${it.value}","name":"test-name","type":"apiKey"}""")
      }
    }
  }
})
