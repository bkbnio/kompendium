package io.bkbn.kompendium.core.schema

import io.bkbn.kompendium.core.Kontent.generateKTypeKontent
import io.bkbn.kompendium.core.Kontent.generateKontent
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.util.Helpers.genericNameAdapter
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.DictionarySchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import org.slf4j.LoggerFactory

object MapHandler : SchemaHandler {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Handler for when a [Map] is encountered
   * @param type Map type information
   * @param clazz Map class information
   * @param cache Existing schema map to append to
   */
  override fun handle(type: KType, clazz: KClass<*>, cache: SchemaMap) {
    logger.debug("Map detected for $type, generating schema and appending to cache")
    val (keyType, valType) = type.arguments.map { it.type }
    logger.debug("Obtained map types -> key: $keyType and value: $valType")
    if (keyType?.classifier != String::class) {
      error("Invalid Map $type: OpenAPI dictionaries must have keys of type String")
    }
    generateKTypeKontent(valType!!, cache)
    val valClass = valType.classifier as KClass<*>
    val valClassName = valClass.simpleName
    val referenceName = genericNameAdapter(type, clazz)
    val valueReference = when (valClass.isSealed) {
      true -> {
        val subTypes = gatherSubTypes(valType)
        AnyOfSchema(subTypes.map {
          generateKTypeKontent(it, cache)
          cache[it.getSimpleSlug()] ?: error("${it.getSimpleSlug()} not found")
        })
      }
      false -> cache[valClassName] ?: error("$valClassName not found")
    }
    val schema = DictionarySchema(additionalProperties = valueReference)
    generateKontent(valType, cache)
    cache[referenceName] = schema
  }
}
