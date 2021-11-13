package io.bkbn.kompendium.core.metadata

import kotlin.reflect.KType
import io.bkbn.kompendium.oas.old.OpenApiSpecResponse

typealias ErrorMap = Map<KType, Pair<Int, OpenApiSpecResponse<*>>?>
