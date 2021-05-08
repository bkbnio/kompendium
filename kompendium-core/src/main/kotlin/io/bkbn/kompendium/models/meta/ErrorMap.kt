package io.bkbn.kompendium.models.meta

import kotlin.reflect.KType
import io.bkbn.kompendium.models.oas.OpenApiSpecResponse

typealias ErrorMap = Map<KType, Pair<Int, OpenApiSpecResponse<*>>?>
