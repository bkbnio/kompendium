package org.leafygreens.kompendium.models.meta

import kotlin.reflect.KType
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse

typealias ErrorMap = Map<KType, Pair<Int, OpenApiSpecResponse<*>>?>
