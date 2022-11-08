package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.SerialNameObject
import io.bkbn.kompendium.core.fixtures.TransientObject
import io.bkbn.kompendium.core.fixtures.UnbackedObject
import io.ktor.server.routing.Routing

fun Routing.ignoredFieldsResponse() = basicGetGenerator<TransientObject>()
fun Routing.unbackedFieldsResponse() = basicGetGenerator<UnbackedObject>()
fun Routing.customFieldNameResponse() = basicGetGenerator<SerialNameObject>()
