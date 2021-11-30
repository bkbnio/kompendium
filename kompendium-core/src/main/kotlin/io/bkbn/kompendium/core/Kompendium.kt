package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.schema.TypedSchema
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlin.reflect.KClass

class Kompendium(val config: Configuration) {

  class Configuration {
    lateinit var spec: OpenApiSpec
    var cache: SchemaMap = emptyMap()

    fun addCustomTypeSchema(clazz: KClass<*>, schema: TypedSchema) {
      cache = cache.plus(clazz.simpleName!! to schema)
    }
  }

  companion object Feature : ApplicationFeature<Application, Configuration, Kompendium> {
    override val key: AttributeKey<Kompendium> = AttributeKey("Kompendium")
    override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kompendium {
      val configuration = Configuration().apply(configure)

      return Kompendium(configuration)
    }
  }
}
