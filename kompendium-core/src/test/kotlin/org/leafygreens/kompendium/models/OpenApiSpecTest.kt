package org.leafygreens.kompendium.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import org.leafygreens.kompendium.util.TestData

internal class OpenApiSpecTest {

  private val mapper = jacksonObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .writerWithDefaultPrettyPrinter()

  @Test
  fun `OpenApiSpec can be serialized into a valid Open API Spec`() {
    // when
    val spec = TestData.testSpec

    // do
    val json = mapper.writeValueAsString(spec)

    // expect
    val expected = TestData.getFileSnapshot("petstore.json").trim()
    assertEquals(expected, json, "Should serialize an empty spec")
  }
}
