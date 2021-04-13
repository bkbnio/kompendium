package org.leafygreens.kompendium.util

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.util.InternalAPI

object Helpers {

  @OptIn(InternalAPI::class)
  fun Route.calculatePath(tail: String = ""): String = when (selector) {
    is RootRouteSelector -> tail
    is PathSegmentParameterRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/{$selector}$tail"
    is PathSegmentConstantRouteSelector -> parent?.calculatePath("/$selector$tail") ?: "/$selector$tail"
    else -> error("unknown selector type $selector")
  }

  fun <K, V> MutableMap<K, V>.putPairIfAbsent(pair: Pair<K, V>) = putIfAbsent(pair.first, pair.second)

}
