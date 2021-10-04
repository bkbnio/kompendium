package io.bkbn.kompendium.path

import io.ktor.routing.PathSegmentConstantRouteSelector
import io.ktor.routing.PathSegmentParameterRouteSelector
import io.ktor.routing.RootRouteSelector
import io.ktor.routing.Route
import io.ktor.routing.TrailingSlashRouteSelector
import io.ktor.util.InternalAPI
import org.slf4j.LoggerFactory

/**
 * Default [PathCalculator] meant to be overridden as necessary
 */
open class CorePathCalculator : PathCalculator {

  private val logger = LoggerFactory.getLogger(javaClass)

  @OptIn(InternalAPI::class)
  override fun calculate(
    route: Route?,
    tail: String
  ): String = when (route) {
    null -> tail
    else -> when (route.selector) {
      is RootRouteSelector -> {
        logger.debug("Root route detected, returning path: $tail")
        tail
      }
      is PathSegmentParameterRouteSelector -> {
        logger.debug("Found segment parameter ${route.selector}, continuing to parent")
        val newTail = "/${route.selector}$tail"
        calculate(route.parent, newTail)
      }
      is PathSegmentConstantRouteSelector -> {
        logger.debug("Found segment constant ${route.selector}, continuing to parent")
        val newTail = "/${route.selector}$tail"
        calculate(route.parent, newTail)
      }
      is TrailingSlashRouteSelector -> {
        logger.debug("Found trailing slash route selector")
        val newTail = tail.ifBlank { "/" }
        calculate(route.parent, newTail)
      }
      else -> handleCustomSelectors(route, tail)
    }
  }

  override fun handleCustomSelectors(route: Route, tail: String): String = error("Unknown selector ${route.selector}")

}
