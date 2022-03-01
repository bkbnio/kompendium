package io.bkbn.kompendium.swagger

internal const val JS_UNDEFINED = "undefined"

internal fun String?.toJs(): String = this?.let { "'$it'" } ?: JS_UNDEFINED
internal fun Boolean?.toJs(): String = this?.toString() ?: JS_UNDEFINED
internal fun Int?.toJs(): String = this?.toString() ?: JS_UNDEFINED

internal fun Any?.toJs(): String = when(this) {
  is String? -> toJs()
  is Int? -> toJs()
  is Boolean? -> toJs()
  else -> toString().toJs()
}
