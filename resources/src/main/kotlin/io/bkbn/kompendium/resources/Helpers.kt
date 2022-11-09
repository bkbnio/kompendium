package io.bkbn.kompendium.resources

import io.ktor.resources.Resource
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

fun KClass<*>.getResourcePathFromClass(): String {
  val resource = findAnnotation<Resource>()
    ?: error("Cannot notarize a resource without annotating with @Resource")

  val path = resource.path
  val parent = memberProperties.map { it.returnType.classifier as KClass<*> }.find { it.hasAnnotation<Resource>() }

  return if (parent == null) {
    path
  } else {
    parent.getResourcePathFromClass() + path
  }
}
