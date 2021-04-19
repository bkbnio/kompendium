package org.leafygreens.kompendium

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey

class KompendiumConfiguration(configuration: Configuration) {

  val prop = configuration.prop

  class Configuration {
    var prop = "value"
  }

  companion object Feature :
    ApplicationFeature<ApplicationCallPipeline, KompendiumConfiguration.Configuration, KompendiumConfiguration> {
    override val key = AttributeKey<KompendiumConfiguration>("KompendiumConfiguration")

    override fun install(
      pipeline: ApplicationCallPipeline,
      configure: Configuration.() -> Unit
    ): KompendiumConfiguration {
      TODO("Not yet implemented")
    }

  }

}
