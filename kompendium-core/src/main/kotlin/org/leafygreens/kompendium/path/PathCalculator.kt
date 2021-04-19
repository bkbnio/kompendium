package org.leafygreens.kompendium.path

import io.ktor.routing.Route

interface PathCalculator {

  fun calculate(route: Route?, tail: String = ""): String

  fun handleUnknownSelector(route: Route): String

}
