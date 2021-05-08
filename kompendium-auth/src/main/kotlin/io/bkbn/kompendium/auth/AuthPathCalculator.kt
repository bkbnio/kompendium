package io.bkbn.kompendium.auth

import io.ktor.auth.AuthenticationRouteSelector
import io.ktor.routing.Route
import io.bkbn.kompendium.path.CorePathCalculator
import org.slf4j.LoggerFactory

class AuthPathCalculator : CorePathCalculator() {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun handleCustomSelectors(route: Route, tail: String): String = when (route.selector) {
    is AuthenticationRouteSelector -> {
      logger.debug("Found authentication route selector ${route.selector}")
      super.calculate(route.parent, tail)
    }
    else -> super.handleCustomSelectors(route, tail)
  }
}
