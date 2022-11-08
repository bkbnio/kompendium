package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.Barzo
import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.Flibbity
import io.bkbn.kompendium.core.fixtures.FlibbityGibbit
import io.bkbn.kompendium.core.fixtures.Foosy
import io.bkbn.kompendium.core.fixtures.Gibbity
import io.bkbn.kompendium.core.fixtures.MultiNestedGenerics
import io.bkbn.kompendium.core.fixtures.Page
import io.ktor.server.routing.Routing

fun Routing.polymorphicResponse() = basicGetGenerator<FlibbityGibbit>()
fun Routing.polymorphicCollectionResponse() = basicGetGenerator<List<FlibbityGibbit>>()
fun Routing.polymorphicMapResponse() = basicGetGenerator<Map<String, FlibbityGibbit>>()
fun Routing.simpleGenericResponse() = basicGetGenerator<Gibbity<String>>()
fun Routing.gnarlyGenericResponse() = basicGetGenerator<Foosy<Barzo<Int>, String>>()
fun Routing.nestedGenericResponse() = basicGetGenerator<Gibbity<Map<String, String>>>()
fun Routing.genericPolymorphicResponse() = basicGetGenerator<Flibbity<Double>>()
fun Routing.genericPolymorphicResponseMultipleImpls() = basicGetGenerator<Flibbity<FlibbityGibbit>>()
fun Routing.nestedGenericCollection() = basicGetGenerator<Page<Int>>()
fun Routing.nestedGenericMultipleParamsCollection() = basicGetGenerator<MultiNestedGenerics<String, ComplexRequest>>()
