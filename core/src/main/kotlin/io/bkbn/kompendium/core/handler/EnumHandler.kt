package io.bkbn.kompendium.core.handler

import io.bkbn.kompendium.core.legacy.metadata.SchemaMap
import io.bkbn.kompendium.oas.schema.EnumSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType

object EnumHandler : SchemaHandler {

  /**
   * Handler for when an [Enum] is encountered
   * @param type Map type information
   * @param clazz Class of the object to analyze
   * @param cache Existing schema map to append to
   */
  override fun handle(type: KType, clazz: KClass<*>, cache: SchemaMap) {
    val options = clazz.java.enumConstants.map { it.toString() }.toSet()
    cache[clazz.simpleName!!] = EnumSchema(options)
  }
}
