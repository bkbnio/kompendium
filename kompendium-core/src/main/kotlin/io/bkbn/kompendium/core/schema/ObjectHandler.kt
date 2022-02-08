package io.bkbn.kompendium.core.schema

import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.FreeFormObject
import io.bkbn.kompendium.annotations.Referenced
import io.bkbn.kompendium.annotations.UndeclaredField
import io.bkbn.kompendium.annotations.constraint.MaxProperties
import io.bkbn.kompendium.annotations.constraint.MinProperties
import io.bkbn.kompendium.core.Kontent
import io.bkbn.kompendium.core.Kontent.generateKontent
import io.bkbn.kompendium.core.constraint.scanForConstraints
import io.bkbn.kompendium.core.metadata.SchemaMap
import io.bkbn.kompendium.core.metadata.TypeMap
import io.bkbn.kompendium.core.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.oas.schema.AnyOfSchema
import io.bkbn.kompendium.oas.schema.ComponentSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import org.slf4j.LoggerFactory

object ObjectHandler : SchemaHandler {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * In the event of an object type, this method will parse out individual fields to recursively aggregate object map.
   * @param type Map type information
   * @param clazz Class of the object to analyze
   * @param cache Existing schema map to append to
   */
  override fun handle(type: KType, clazz: KClass<*>, cache: SchemaMap) {
    // This needs to be simple because it will be stored under its appropriate reference component implicitly
    val slug = type.getSimpleSlug()
    // Only analyze if component has not already been stored in the cache
    if (!cache.containsKey(slug)) {
      logger.debug("$slug was not found in cache, generating now")
      // TODO Replace with something better!
      // If referenced, add tie from simple slug to schema slug
      if (clazz.hasAnnotation<Referenced>()) {
        cache[type.getSimpleSlug()] = ReferencedSchema(type.getReferenceSlug())
      }
      // Grabs any type parameters mapped to the corresponding type argument(s)
      val typeMap: TypeMap = clazz.typeParameters.zip(type.arguments).toMap()
      // associates each member with a Pair of prop name to property schema
      val fieldMap = clazz.memberProperties.associate { prop ->
        logger.debug("Analyzing $prop in class $clazz")

        // Grab the field of the current property
        val field = prop.javaField?.type?.kotlin ?: error("Unable to parse field type from $prop")

        // Short circuit if data is free form
        val freeForm = prop.findAnnotation<FreeFormObject>()
        var name = prop.name

        when (freeForm) {
          null -> {
            val bleh = handleDefault(typeMap, prop, cache)
            bleh.first
          }
          else -> handleFreeForm(prop)
        }
      }
      logger.debug("Looking for undeclared fields")
      val undeclaredFieldMap = clazz.annotations.filterIsInstance<UndeclaredField>().associate {
        val undeclaredType = it.clazz.createType()
        generateKontent(undeclaredType, cache)
        it.field to cache[undeclaredType.getSimpleSlug()]!!
      }
      logger.debug("$slug contains $fieldMap")
      var schema = ObjectSchema(fieldMap.plus(undeclaredFieldMap))
      val requiredParams = clazz.primaryConstructor?.parameters?.filterNot { it.isOptional } ?: emptyList()
      // todo de-dup this logic
      if (requiredParams.isNotEmpty()) {
        schema = schema.copy(required = requiredParams.map { param ->
          clazz.memberProperties.first { it.name == param.name }.findAnnotation<Field>()
            ?.let { field -> field.name.ifBlank { param.name!! } }
            ?: param.name!!
        })
      }
      logger.debug("$slug schema: $schema")
      cache[slug] = schema
    }
  }

  // TODO Better type, or just make map mutable
  private fun handleDefault(
    typeMap: TypeMap,
    prop: KProperty1<*, *>,
    cache: SchemaMap
  ): Pair<Pair<String, ComponentSchema>, SchemaMap> {
    var name = prop.name
    val field = prop.javaField?.type?.kotlin ?: error("Unable to parse field type from $prop")
    val baseType = scanForGeneric(typeMap, prop)
    val baseClazz = baseType.classifier as KClass<*>
    val allTypes = scanForSealed(baseClazz, baseType)
    updateCache(cache, field, allTypes)
    var propSchema = constructComponentSchema(
      typeMap = typeMap,
      prop = prop,
      fieldClazz = field,
      clazz = baseClazz,
      type = baseType,
      cache = cache
    )
    // todo move to helper
    prop.findAnnotation<Field>()?.let { fieldOverrides ->
      if (fieldOverrides.description.isNotBlank()) {
        propSchema = propSchema.setDescription(fieldOverrides.description)
      }
      if (fieldOverrides.name.isNotBlank()) {
        name = fieldOverrides.name
      }
    }
    return Pair(Pair(name, propSchema), cache)
  }

  private fun handleFreeForm(prop: KProperty1<*, *>): Pair<String, FreeFormSchema> {
    val minProperties = prop.findAnnotation<MinProperties>()
    val maxProperties = prop.findAnnotation<MaxProperties>()
    val schema = FreeFormSchema(
      minProperties = minProperties?.properties,
      maxProperties = maxProperties?.properties
    )
    return Pair(prop.name, schema)
  }

  /**
   * Yoinks any generic types from the type map should the field be a generic
   */
  private fun scanForGeneric(typeMap: TypeMap, prop: KProperty1<*, *>): KType =
    if (typeMap.containsKey(prop.returnType.classifier)) {
      logger.debug("Generic type detected")
      typeMap[prop.returnType.classifier]?.type!!
    } else {
      prop.returnType
    }

  /**
   * Scans a class for sealed subclasses.  If found, returns a list with all children.  Otherwise, returns
   * the base type
   */
  private fun scanForSealed(clazz: KClass<*>, type: KType): List<KType> = if (clazz.isSealed) {
    clazz.sealedSubclasses.map { it.createType(type.arguments) }
  } else {
    listOf(type)
  }

  /**
   * Takes the type information provided and adds any missing data to the schema map
   */
  private fun updateCache(cache: SchemaMap, clazz: KClass<*>, types: List<KType>) {
    if (!cache.containsKey(clazz.simpleName)) {
      logger.debug("Cache was missing ${clazz.simpleName}, adding now")
      types.forEach {
        Kontent.generateKTypeKontent(it, cache)
      }
    }
  }

  private fun constructComponentSchema(
    typeMap: TypeMap,
    clazz: KClass<*>,
    fieldClazz: KClass<*>,
    prop: KProperty1<*, *>,
    type: KType,
    cache: SchemaMap
  ): ComponentSchema =
    when (typeMap.containsKey(prop.returnType.classifier)) {
      true -> handleGenericProperty(typeMap, clazz, type, prop.returnType.classifier, cache)
      false -> handleStandardProperty(clazz, fieldClazz, prop, type, cache)
    }.scanForConstraints(clazz, prop)

  /**
   * If a field has type parameters, leverage the constructed [TypeMap] to construct the [ComponentSchema]
   */
  private fun handleGenericProperty(
    typeMap: TypeMap,
    clazz: KClass<*>,
    type: KType,
    classifier: KClassifier?,
    cache: SchemaMap
  ): ComponentSchema = if (clazz.isSealed) {
    val refs = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { it.getSimpleSlug() }
      .map { cache[it] ?: error("$it not available in cache") }
    AnyOfSchema(refs)
  } else {
    val slug = typeMap[classifier]?.type!!.getSimpleSlug()
    cache[slug] ?: error("$slug not found in cache")
  }

  /**
   * If a field has no type parameters, build its [ComponentSchema] without referencing the [TypeMap]
   */
  private fun handleStandardProperty(
    clazz: KClass<*>,
    fieldClazz: KClass<*>,
    prop: KProperty1<*, *>,
    type: KType,
    cache: SchemaMap
  ): ComponentSchema = if (clazz.isSealed) {
    val refs = clazz.sealedSubclasses
      .map { it.createType(type.arguments) }
      .map { cache[it.getSimpleSlug()] ?: error("$it not found in cache") }
    AnyOfSchema(refs)
  } else {
    val slug = fieldClazz.getSimpleSlug(prop)
    cache[slug] ?: error("$slug not found in cache")
  }
}
