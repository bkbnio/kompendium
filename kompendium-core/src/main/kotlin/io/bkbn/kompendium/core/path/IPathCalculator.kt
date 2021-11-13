package io.bkbn.kompendium.core.path

import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import kotlin.reflect.KClass

interface IPathCalculator {
  fun calculate(route: Route?, tail: String = ""): String
  fun <T : RouteSelector> addCustomRouteHandler(
    selector: KClass<T>,
    handler: IPathCalculator.(Route, String) -> String
  )
}
