package io.bkbn.kompendium.swagger

import kotlin.reflect.full.memberProperties

internal inline fun<reified T: Any> T.asMap(): Map<String, Any?> =
  T::class.memberProperties.associate { it.name to it.get(this) }
