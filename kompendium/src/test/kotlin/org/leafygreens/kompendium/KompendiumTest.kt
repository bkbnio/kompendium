package org.leafygreens.kompendium

import kotlin.test.Test
import kotlin.test.assertEquals

internal class KompendiumTest {

  @Test
  fun `Kompendium can be instantiated with no details`() {
    val kompendium = Kompendium()
    assertEquals(kompendium.spec.openapi, "3.0.3", "Kompendium has a default spec version of 3.0.3")
  }

}
