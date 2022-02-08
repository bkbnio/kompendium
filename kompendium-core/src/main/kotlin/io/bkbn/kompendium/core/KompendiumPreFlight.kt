package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.util.Helpers.COMPONENT_SLUG
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.DictionarySchema
import io.bkbn.kompendium.oas.schema.EmptySchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import io.ktor.application.feature
import io.ktor.routing.Route
import io.ktor.routing.application
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Functions are considered preflight when they are used to intercept a method ahead of running.
 */
object KompendiumPreFlight {

  /**
   * Performs all content analysis on the types provided to a notarized route and adds it to the top level spec
   * @param TParam
   * @param TReq
   * @param TResp
   * @param block The function to execute, provided type information of the parameters above
   * @return [Route]
   */
  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified TParam : Any, reified TReq : Any, reified TResp : Any> Route.methodNotarizationPreFlight(
    block: (KType, KType, KType) -> Route
  ): Route {
    val feature = this.application.feature(Kompendium)
    val requestType = typeOf<TReq>()
    val responseType = typeOf<TResp>()
    val paramType = typeOf<TParam>()
    addToCache(paramType, requestType, responseType, feature)
    return block.invoke(paramType, requestType, responseType)
  }

  fun addToCache(paramType: KType, requestType: KType, responseType: KType, feature: Kompendium) {
    feature.config.cache = Kontent.generateKontent(requestType, feature.config.cache)
    feature.config.cache = Kontent.generateKontent(responseType, feature.config.cache)
    feature.config.cache = Kontent.generateKontent(paramType, feature.config.cache)
    feature.updateReferences()
  }

  private fun Kompendium.updateReferences() {
    val references = config.cache.values
      .asSequence()
      .map { flattenSchema(it) }
      .flatten()
      .filterIsInstance<ReferencedSchema>()
      .map { it.`$ref` }
      .map { it.replace(COMPONENT_SLUG.plus("/"), "") }
      .toList()
    references.forEach { ref ->
      config.spec.components.schemas[ref] = config.cache[ref] ?: error("$ref does not exist in cache 😱")
    }
  }

  private fun flattenSchema(schema: ComponentSchema): List<ComponentSchema> = when (schema) {
    is AnyOfSchema -> schema.anyOf.map { flattenSchema(it) }.flatten()
    is ReferencedSchema -> listOf(schema)
    is ArraySchema -> flattenSchema(schema.items)
    is DictionarySchema -> flattenSchema(schema.additionalProperties)
    is EnumSchema -> listOf(schema)
    is FormattedSchema -> listOf(schema)
    is FreeFormSchema -> listOf(schema)
    is ObjectSchema -> schema.properties.values.map { flattenSchema(it) }.flatten()
    is SimpleSchema -> listOf(schema)
    EmptySchema -> error("There should be no empty schemas at this point! This indicates a library bug!")
  }
}
