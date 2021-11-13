package io.bkbn.kompendium.core.path

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import io.ktor.routing.TrailingSlashRouteSelector
import io.ktor.util.InternalAPI
import kotlin.reflect.KClass

/**
 * Responsible for calculating a url path from a provided [Route]
 */
@OptIn(InternalAPI::class)
internal object PathCalculator: IPathCalculator {

  private val pathHandler: RouteHandlerMap = mutableMapOf()

  init {
    addCustomRouteHandler(RootRouteSelector::class) { _, tail -> tail }
    addCustomRouteHandler(PathSegmentParameterRouteSelector::class) { route, tail ->
      val newTail = "/${route.selector}$tail"
      calculate(route.parent, newTail)
    }
    addCustomRouteHandler(PathSegmentConstantRouteSelector::class) { route, tail ->
      val newTail = "/${route.selector}$tail"
      calculate(route.parent, newTail)
    }
    addCustomRouteHandler(TrailingSlashRouteSelector::class) { route, tail ->
      val newTail = tail.ifBlank { "/" }
      calculate(route.parent, newTail)
    }
  }

  @OptIn(InternalAPI::class)
  override fun calculate(
    route: Route?,
    tail: String
  ): String = when (route) {
    null -> tail
    else -> when (pathHandler.containsKey(route.selector::class)) {
      true -> pathHandler[route.selector::class]!!.invoke(this, route, tail)
      else -> error("No handler has been registered for ${route.selector}")
    }
  }

  override fun <T : RouteSelector> addCustomRouteHandler(
    selector: KClass<T>,
    handler: IPathCalculator.(Route, String) -> String
  ) {
    pathHandler[selector] = handler
  }
}
