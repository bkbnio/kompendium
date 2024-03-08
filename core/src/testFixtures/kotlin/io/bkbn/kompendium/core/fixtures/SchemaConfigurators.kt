package io.bkbn.kompendium.core.fixtures

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.definition.JsonSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/*
  These are test implementation and may well be a good starting point for creating production ones.
  Both Gson and Jackson are complex and can achieve this things is more than one way therefore
  these will not always work hence why they are in the test package
 */

class GsonSchemaConfigurator: SchemaConfigurator {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> {
    // NOTE: This is test logic Expose is set at a global Gson level so configure to match your Gson set up
    val hasAnyExpose = clazz.memberProperties.any { it.hasJavaAnnotation<Expose>() }
    return if(hasAnyExpose) {
      clazz.memberProperties
        .filter { it.hasJavaAnnotation<Expose>() }
    } else clazz.memberProperties
  }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.getJavaAnnotation<SerializedName>()?.value?: property.name

  override fun sealedObjectEnrichment(implementationType: KType, implementationSchema: JsonSchema) = TODO()
}

class JacksonSchemaConfigurator: SchemaConfigurator {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> =
    clazz.memberProperties
      .filterNot {
        it.hasJavaAnnotation<JsonIgnore>()
      }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.getJavaAnnotation<JsonProperty>()?.value?: property.name

  override fun sealedObjectEnrichment(implementationType: KType, implementationSchema: JsonSchema) = TODO()
}

inline fun <reified T: Annotation> KProperty1<*, *>.hasJavaAnnotation(): Boolean {
  return javaField?.isAnnotationPresent(T::class.java)?: false
}

inline fun <reified T: Annotation> KProperty1<*, *>.getJavaAnnotation(): T? {
  return javaField?.getDeclaredAnnotation(T::class.java)
}
