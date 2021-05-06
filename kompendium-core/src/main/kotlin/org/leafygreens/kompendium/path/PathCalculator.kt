package org.leafygreens.kompendium.path

import io.ktor.routing.Route

/**
 * Extensible interface for calculating Ktor paths
 */
interface PathCalculator {

  /**
   * Core route calculation method
   */
  fun calculate(route: Route?, tail: String = ""): String

  /**
   * Used to handle any custom selectors that may be missed by the base route calculation
   */
  fun handleCustomSelectors(route: Route, tail: String): String

}
