package io.bkbn.kompendium.core.constraint

import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.constraint.Format
import io.bkbn.kompendium.annotations.constraint.MaxItems
import io.bkbn.kompendium.annotations.constraint.MaxLength
import io.bkbn.kompendium.annotations.constraint.Maximum
import io.bkbn.kompendium.annotations.constraint.MinItems
import io.bkbn.kompendium.annotations.constraint.MinLength
import io.bkbn.kompendium.annotations.constraint.Minimum
import io.bkbn.kompendium.annotations.constraint.MultipleOf
import io.bkbn.kompendium.annotations.constraint.Pattern
import io.bkbn.kompendium.annotations.constraint.UniqueItems
import io.bkbn.kompendium.core.util.Helpers.toNumber
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
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun ComponentSchema.scanForConstraints(clazz: KClass<*>, prop: KProperty1<*, *>): ComponentSchema =
  when (this) {
    is AnyOfSchema -> AnyOfSchema(anyOf.map { it.scanForConstraints(clazz, prop) })
    is ArraySchema -> scanForConstraints(prop)
    is DictionarySchema -> this // TODO Anything here?
    is EnumSchema -> scanForConstraints(prop)
    is FormattedSchema -> scanForConstraints(prop)
    is FreeFormSchema -> this // todo anything here?
    is ObjectSchema -> scanForConstraints(clazz, prop)
    is SimpleSchema -> scanForConstraints(prop)
    is ReferencedSchema -> this // todo anything here?
    EmptySchema -> error("Empty Schema cannot be scanned for constraints!")
  }

fun ArraySchema.scanForConstraints(prop: KProperty1<*, *>): ArraySchema {
  val minItems = prop.findAnnotation<MinItems>()
  val maxItems = prop.findAnnotation<MaxItems>()
  val uniqueItems = prop.findAnnotation<UniqueItems>()

  return this.copy(
    minItems = minItems?.items,
    maxItems = maxItems?.items,
    uniqueItems = uniqueItems?.let { true }
  )
}

fun EnumSchema.scanForConstraints(prop: KProperty1<*, *>): EnumSchema {
  if (prop.returnType.isMarkedNullable) {
    return this.copy(nullable = true)
  }

  return this
}

fun FormattedSchema.scanForConstraints(prop: KProperty1<*, *>): FormattedSchema {
  val minimum = prop.findAnnotation<Minimum>()
  val maximum = prop.findAnnotation<Maximum>()
  val multipleOf = prop.findAnnotation<MultipleOf>()

  var schema = this

  if (prop.returnType.isMarkedNullable) {
    schema = schema.copy(nullable = true)
  }

  return schema.copy(
    minimum = minimum?.min?.toNumber(),
    maximum = maximum?.max?.toNumber(),
    exclusiveMinimum = minimum?.exclusive,
    exclusiveMaximum = maximum?.exclusive,
    multipleOf = multipleOf?.multiple?.toNumber(),
  )
}

fun SimpleSchema.scanForConstraints(prop: KProperty1<*, *>): SimpleSchema {
  val minLength = prop.findAnnotation<MinLength>()
  val maxLength = prop.findAnnotation<MaxLength>()
  val pattern = prop.findAnnotation<Pattern>()
  val format = prop.findAnnotation<Format>()

  var schema = this

  if (prop.returnType.isMarkedNullable) {
    schema = schema.copy(nullable = true)
  }

  return schema.copy(
    minLength = minLength?.length,
    maxLength = maxLength?.length,
    pattern = pattern?.pattern,
    format = format?.format
  )
}

fun ObjectSchema.scanForConstraints(clazz: KClass<*>, prop: KProperty1<*, *>): ObjectSchema {
  val requiredParams = clazz.primaryConstructor?.parameters?.filterNot { it.isOptional } ?: emptyList()
  var schema = this

  // todo dedup this
  if (requiredParams.isNotEmpty()) {
    schema = schema.copy(required = requiredParams.map { param ->
      clazz.memberProperties.first { it.name == param.name }.findAnnotation<Field>()
        ?.let { field -> field.name.ifBlank { param.name!! } }
        ?: param.name!!
    })
  }

  if (prop.returnType.isMarkedNullable) {
    schema = schema.copy(nullable = true)
  }

  return schema
}
