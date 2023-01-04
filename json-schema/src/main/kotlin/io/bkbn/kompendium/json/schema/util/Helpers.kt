package io.bkbn.kompendium.json.schema.util

import io.bkbn.kompendium.enrichment.Enrichment
import io.bkbn.kompendium.enrichment.PropertyEnrichment
import io.bkbn.kompendium.enrichment.TypeEnrichment
import kotlin.reflect.KClass
import kotlin.reflect.KType

object Helpers {

  private const val COMPONENT_SLUG = "#/components/schemas"

  fun KType.getSlug(enrichment: Enrichment? = null) = when (enrichment) {
    is TypeEnrichment<*> -> getEnrichedSlug(enrichment)
    is PropertyEnrichment -> error("Slugs should not be generated for field enrichments")
    null -> getSimpleSlug()
  }

  fun KType.getSimpleSlug(): String = when {
    this.arguments.isNotEmpty() -> genericNameAdapter(this, classifier as KClass<*>)
    else -> (classifier as KClass<*>).kompendiumSlug() ?: error("Could not determine simple name for $this")
  }

  private fun KType.getEnrichedSlug(enrichment: TypeEnrichment<*>) = getSimpleSlug() + "-${enrichment.id}"

  fun KType.getReferenceSlug(enrichment: Enrichment? = null): String = when (enrichment) {
    is TypeEnrichment<*> -> getSimpleReferenceSlug() + "-${enrichment.id}"
    is PropertyEnrichment -> error("Reference slugs should never be generated for field enrichments")
    null -> getSimpleReferenceSlug()
  }

  private fun KType.getSimpleReferenceSlug() = when {
    arguments.isNotEmpty() -> "$COMPONENT_SLUG/${genericNameAdapter(this, classifier as KClass<*>)}"
    else -> "$COMPONENT_SLUG/${(classifier as KClass<*>).kompendiumSlug()}"
  }

  @Suppress("ReturnCount")
  private fun KClass<*>.kompendiumSlug(): String? {
    if (java.packageName == "java.lang") return simpleName
    if (java.packageName == "java.util") return simpleName
    val pkg = java.packageName
    return qualifiedName?.replace(pkg, "")?.replace(".", "")
  }

  private fun genericNameAdapter(type: KType, clazz: KClass<*>): String {
    val classNames = type.arguments
      .map { it.type?.classifier as KClass<*> }
      .map { it.kompendiumSlug() }
    return classNames.joinToString(separator = "-", prefix = "${clazz.kompendiumSlug()}-")
  }
}
