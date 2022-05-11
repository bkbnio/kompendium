package io.bkbn.kompendium.auth

object Notarized {

//  fun Route.notarizedAuthenticate(
//    vararg configurations: SecurityConfiguration,
//    optional: Boolean = false,
//    build: Route.() -> Unit
//  ): Route {
//    val configurationNames = configurations.map { it.name }.toTypedArray()
//    val feature = application.feature(Kompendium)
//
//    configurations.forEach { config ->
//      feature.config.spec.components.securitySchemes[config.name] = when (config) {
//        is ApiKeyConfiguration -> ApiKeyAuth(config.location, config.keyName)
//        is BasicAuthConfiguration -> BasicAuth()
//        is JwtAuthConfiguration -> BearerAuth(config.bearerFormat)
//        is OAuthConfiguration -> OAuth(config.description, config.flows)
//      }
//    }
//
//    return authenticate(*configurationNames, optional = optional, build = build)
//  }

}
