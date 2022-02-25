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
import io.bkbn.kompendium.oas.schema.EnumSchema
import io.bkbn.kompendium.oas.schema.FormattedSchema
import io.bkbn.kompendium.oas.schema.FreeFormSchema
import io.bkbn.kompendium.oas.schema.ObjectSchema
import io.bkbn.kompendium.oas.schema.ReferencedSchema
import io.bkbn.kompendium.oas.schema.SimpleSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun ComponentSchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): ComponentSchema =
  when (this) {
    is AnyOfSchema -> scanForConstraints(type, prop)
    is ArraySchema -> scanForConstraints(type, prop)
    is DictionarySchema -> this // TODO Anything here?
    is EnumSchema -> scanForConstraints(prop)
    is FormattedSchema -> scanForConstraints(type, prop)
    is FreeFormSchema -> this // todo anything here?
    is ObjectSchema -> scanForConstraints(type, prop)
    is SimpleSchema -> scanForConstraints(type, prop)
    is ReferencedSchema -> this // todo anything here?
  }

fun AnyOfSchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): AnyOfSchema {
  val anyOf = anyOf.map { it.scanForConstraints(type, prop) }
  return this.copy(
    anyOf = anyOf
  )
}

fun ArraySchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): ArraySchema {
  val minItems = prop.findAnnotation<MinItems>()?.items ?: this.minItems
  val maxItems = prop.findAnnotation<MaxItems>()?.items ?: this.minItems
  val uniqueItems = prop.findAnnotation<UniqueItems>()?.let { true } ?: this.uniqueItems
  val items = items.scanForConstraints(type, prop)

  return this.copy(
    minItems = minItems,
    maxItems = maxItems,
    uniqueItems = uniqueItems,
    items = items
  )
}

fun EnumSchema.scanForConstraints(prop: KProperty1<*, *>): EnumSchema {
  if (prop.returnType.isMarkedNullable) {
    return this.copy(nullable = true)
  }

  return this
}

fun FormattedSchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): FormattedSchema {
  val minimum = prop.findAnnotation<Minimum>()?.min?.toNumber() ?: this.minimum
  val exclusiveMinimum = prop.findAnnotation<Minimum>()?.exclusive ?: this.exclusiveMinimum
  val maximum = prop.findAnnotation<Maximum>()?.max?.toNumber() ?: this.maximum
  val exclusiveMaximum = prop.findAnnotation<Maximum>()?.exclusive ?: this.exclusiveMaximum
  val multipleOf = prop.findAnnotation<MultipleOf>()?.multiple?.toNumber() ?: this.multipleOf
  val format = type.arguments.firstOrNull()?.type?.findAnnotation<Format>()?.format
    ?: prop.findAnnotation<Format>()?.format ?: this.format
  val nullable = if (prop.returnType.isMarkedNullable) true else this.nullable

  return this.copy(
    minimum = minimum,
    maximum = maximum,
    exclusiveMinimum = exclusiveMinimum,
    exclusiveMaximum = exclusiveMaximum,
    multipleOf = multipleOf,
    nullable = nullable,
    format = format
  )
}

fun SimpleSchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): SimpleSchema {
  val minLength = prop.findAnnotation<MinLength>()?.length ?: this.minLength
  val maxLength = prop.findAnnotation<MaxLength>()?.length ?: this.maxLength
  val pattern = prop.findAnnotation<Pattern>()?.pattern ?: this.pattern
  val format = type.arguments.firstOrNull()?.type?.findAnnotation<Format>()?.format
    ?: prop.findAnnotation<Format>()?.format ?: this.format
  val nullable = if (prop.returnType.isMarkedNullable) true else this.nullable

  return this.copy(
    minLength = minLength,
    maxLength = maxLength,
    pattern = pattern,
    format = format,
    nullable = nullable
  )
}

fun ObjectSchema.scanForConstraints(type: KType, prop: KProperty1<*, *>): ObjectSchema {
  val clazz = type.classifier as KClass<*>
  var schema = this.adjustForRequiredParams(clazz)
  if (prop.returnType.isMarkedNullable) {
    schema = schema.copy(nullable = true)
  }

  return schema
}

fun ObjectSchema.adjustForRequiredParams(clazz: KClass<*>): ObjectSchema {
  val requiredParams = clazz.primaryConstructor?.parameters?.filterNot { it.isOptional } ?: emptyList()
  var schema = this
  if (requiredParams.isNotEmpty()) {
    schema = schema.copy(required = requiredParams.map { param ->
      clazz.memberProperties.first { it.name == param.name }.findAnnotation<Field>()
        ?.let { field -> field.name.ifBlank { param.name!! } }
        ?: param.name!!
    })
  }
  return schema
}
