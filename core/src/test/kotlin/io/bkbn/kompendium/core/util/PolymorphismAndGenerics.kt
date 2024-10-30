package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.fixtures.Barzo
import io.bkbn.kompendium.core.fixtures.ChillaxificationMaximization
import io.bkbn.kompendium.core.fixtures.ComplexRequest
import io.bkbn.kompendium.core.fixtures.Flibbity
import io.bkbn.kompendium.core.fixtures.FlibbityGibbit
import io.bkbn.kompendium.core.fixtures.Foosy
import io.bkbn.kompendium.core.fixtures.Gibbity
import io.bkbn.kompendium.core.fixtures.Gizmo
import io.bkbn.kompendium.core.fixtures.MultiNestedGenerics
import io.bkbn.kompendium.core.fixtures.Page
import io.ktor.server.routing.Route

fun Route.polymorphicResponse() = basicGetGenerator<FlibbityGibbit>()
fun Route.polymorphicCollectionResponse() = basicGetGenerator<List<FlibbityGibbit>>()
fun Route.polymorphicMapResponse() = basicGetGenerator<Map<String, FlibbityGibbit>>()
fun Route.simpleGenericResponse() = basicGetGenerator<Gibbity<String>>()
fun Route.gnarlyGenericResponse() = basicGetGenerator<Foosy<Barzo<Int>, String>>()
fun Route.nestedGenericResponse() = basicGetGenerator<Gibbity<Map<String, String>>>()
fun Route.genericPolymorphicResponse() = basicGetGenerator<Flibbity<Double>>()
fun Route.genericPolymorphicResponseMultipleImpls() = basicGetGenerator<Flibbity<FlibbityGibbit>>()
fun Route.nestedGenericCollection() = basicGetGenerator<Page<Int>>()
fun Route.nestedGenericMultipleParamsCollection() = basicGetGenerator<MultiNestedGenerics<String, ComplexRequest>>()
fun Route.overrideSealedTypeIdentifier() = basicGetGenerator<ChillaxificationMaximization>()
fun Route.subtypeNotCompleteSetOfParentProperties() = basicGetGenerator<Gizmo>()
