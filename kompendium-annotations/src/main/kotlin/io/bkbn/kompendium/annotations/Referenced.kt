package io.bkbn.kompendium.annotations

/**
 * This instructs Kompendium to store the class as a referenced component.
 * This is mandatory for any data models that have recursive children.
 * If you do not annotate a recursive class with [Referenced], you will
 * get a stack overflow error when you try to launch your API
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Referenced
