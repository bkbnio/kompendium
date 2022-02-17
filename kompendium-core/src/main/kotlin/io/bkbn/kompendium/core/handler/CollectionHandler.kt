package io.bkbn.kompendium.core.handler

import io.bkbn.kompendium.core.Kontent
import io.bkbn.kompendium.core.Kontent.generateKTypeKontent
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.util.Helpers
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ArraySchema
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import org.slf4j.LoggerFactory

object CollectionHandler : SchemaHandler {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Handler for when a [Collection] is encountered
   * @param type Collection type information
   * @param clazz Collection class information
   * @param cache Existing schema map to append to
   */
  override fun handle(type: KType, clazz: KClass<*>, cache: SchemaMap) {
    logger.debug("Collection detected for $type, generating schema and appending to cache")
    val collectionType = type.arguments.first().type!!
    val collectionClass = collectionType.classifier as KClass<*>
    logger.debug("Obtained collection class: $collectionClass")
    val referenceName = Helpers.genericNameAdapter(type, clazz)
    generateKTypeKontent(collectionType, cache)
    val valueReference = when (collectionClass.isSealed) {
      true -> {
        val subTypes = gatherSubTypes(collectionType)
        AnyOfSchema(subTypes.map {
          generateKTypeKontent(it, cache)
          // todo clean this up
          when (val schema = cache[it.getSimpleSlug()] ?: error("${it.getSimpleSlug()} not found")) {
            is ObjectSchema -> ReferencedSchema(Helpers.COMPONENT_SLUG.plus("/").plus(it.getSimpleSlug()))
            is EnumSchema -> ReferencedSchema(Helpers.COMPONENT_SLUG.plus("/").plus(it.getSimpleSlug()))
            else -> schema
          }
        })
      }
      false -> {
        // todo clean up
        when (val schema = cache[collectionClass.simpleName] ?: error("${collectionClass.simpleName} not found")) {
          is ObjectSchema -> ReferencedSchema(Helpers.COMPONENT_SLUG.plus("/").plus(collectionClass.simpleName))
          is EnumSchema -> ReferencedSchema(Helpers.COMPONENT_SLUG.plus("/").plus(collectionClass.simpleName))
          else -> schema
        }
      }
    }
    val schema = ArraySchema(items = valueReference)
    Kontent.generateKontent(collectionType, cache)
    cache[referenceName] = schema
  }
}
