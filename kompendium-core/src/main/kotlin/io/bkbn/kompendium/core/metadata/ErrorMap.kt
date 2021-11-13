package io.bkbn.kompendium.core.metadata

import io.bkbn.kompendium.oas.payload.Response
import kotlin.reflect.KType

typealias ErrorMap = Map<KType, Pair<Int, Response<*>>?>
