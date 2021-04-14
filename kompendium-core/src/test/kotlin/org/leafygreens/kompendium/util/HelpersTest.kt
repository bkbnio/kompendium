package org.leafygreens.kompendium.util

import kotlin.test.Test
import kotlin.test.assertNotNull
import org.leafygreens.kompendium.util.Helpers.objectSchemaPair

internal class HelpersTest {

  @Test
  fun `can build an object schema from a complex object`() {
    // when
    val clazz = ComplexRequest::class

    // do
    val result = objectSchemaPair(clazz)

    // expect
    assertNotNull(result)
  }

}
