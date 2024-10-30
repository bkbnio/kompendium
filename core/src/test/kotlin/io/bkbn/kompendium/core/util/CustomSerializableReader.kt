package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.SerialNameObject
import io.bkbn.kompendium.core.fixtures.TransientObject
import io.bkbn.kompendium.core.fixtures.UnbackedObject
import io.ktor.server.routing.Route

fun Route.ignoredFieldsResponse() = basicGetGenerator<TransientObject>()
fun Route.unbackedFieldsResponse() = basicGetGenerator<UnbackedObject>()
fun Route.customFieldNameResponse() = basicGetGenerator<SerialNameObject>()
