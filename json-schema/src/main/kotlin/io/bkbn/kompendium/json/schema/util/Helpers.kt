package io.bkbn.kompendium.json.schema.util

import io.bkbn.kompendium.enrichment.ApiClass
import io.bkbn.kompendium.enrichment.Enrichment
import kotlin.reflect.KClass
import kotlin.reflect.KType

object Helpers {

  const val COMPONENT_SLUG = "#/components/schemas"

  fun KType.getSlug(enrichment: Enrichment? = null) = when (enrichment) {
    null -> getSimpleSlug()
    else -> getEnrichedSlug(enrichment)
  }

  fun KType.getSimpleSlug(): String = when {
    this.arguments.isNotEmpty() -> genericNameAdapter(this, classifier as KClass<*>)
    else -> (classifier as KClass<*>).kompendiumSlug() ?: error("Could not determine simple name for $this")
  }

  private fun KType.getEnrichedSlug(enrichment: Enrichment) = getSimpleSlug() + "-${enrichment.id}"

  fun KType.getReferenceSlug(enrichment: Enrichment? = null): String = when (enrichment) {
    null -> getSimpleReferenceSlug()
    else -> getSimpleReferenceSlug() + "-${enrichment.id}"
  }

  private fun KType.getSimpleReferenceSlug() = when {
    arguments.isNotEmpty() -> "$COMPONENT_SLUG/${genericNameAdapter(this, classifier as KClass<*>)}"
    else -> "$COMPONENT_SLUG/${(classifier as KClass<*>).kompendiumSlug()}"
  }

  @Suppress("ReturnCount")
  private fun KClass<*>.kompendiumSlug(): String? {
    if (java.packageName == "java.lang") return simpleName
    if (java.packageName == "java.util") return simpleName
    val apiClassAnnotation = annotations.filterIsInstance<ApiClass>().firstOrNull()
    return if (apiClassAnnotation != null) {
      apiClassAnnotation.refId
    } else {
      val pkg = java.packageName
      qualifiedName?.replace(pkg, "")?.replace(".", "")
    }
  }

  private fun genericNameAdapter(type: KType, clazz: KClass<*>): String {
    val classNames = type.arguments
      .map { it.type?.classifier as KClass<*> }
      .map { it.kompendiumSlug() }
    return classNames.joinToString(separator = "-", prefix = "${clazz.kompendiumSlug()}-")
  }
}
