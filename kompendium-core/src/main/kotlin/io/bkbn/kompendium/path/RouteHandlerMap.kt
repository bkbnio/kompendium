package io.bkbn.kompendium.path

import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import kotlin.reflect.KClass

typealias RouteHandlerMap = MutableMap<KClass<out RouteSelector>, IPathCalculator.(Route, String) -> String>
