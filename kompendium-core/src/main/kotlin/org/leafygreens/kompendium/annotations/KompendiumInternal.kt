package org.leafygreens.kompendium.annotations

@Suppress("DEPRECATION")
@RequiresOptIn(
  level = RequiresOptIn.Level.WARNING,
  message = "This API internal to Kompendium and should not be used. It could be removed or changed without notice."
)
@Experimental(level = Experimental.Level.WARNING)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.TYPEALIAS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.FIELD,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.PROPERTY_SETTER
)
annotation class KompendiumInternal
