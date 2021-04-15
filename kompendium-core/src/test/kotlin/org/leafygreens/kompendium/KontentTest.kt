package org.leafygreens.kompendium

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.leafygreens.kompendium.Kontent.generateKontent
import org.leafygreens.kompendium.models.oas.FormatSchema
import org.leafygreens.kompendium.models.oas.ObjectSchema
import org.leafygreens.kompendium.models.oas.ReferencedSchema
import org.leafygreens.kompendium.util.TestInvalidMap
import org.leafygreens.kompendium.util.TestNestedModel
import org.leafygreens.kompendium.util.TestSimpleModel
import org.leafygreens.kompendium.util.TestSimpleWithEnums
import org.leafygreens.kompendium.util.TestSimpleWithList
import org.leafygreens.kompendium.util.TestSimpleWithMap

internal class KontentTest {

  @Test
  fun `Unit returns empty map on generate`() {
    // when
    val clazz = Unit::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertTrue { result.isEmpty() }
  }

  @Test
  fun `Primitive types return a single map result`() {
    // when
    val clazz = Long::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertEquals(1, result.count(), "Should have a single result")
    assertEquals(FormatSchema("int64", "integer"), result["Long"])
  }

  @Test
  fun `Throws an error when top level generics are detected`() {
    // when
    val womp = mapOf("asdf" to "fdsa", "2cool" to "4school")
    val clazz = womp::class

    // expect
    assertFailsWith<IllegalStateException> { generateKontent(clazz) }
  }

  @Test
  fun `Objects reference their base types in the cache`() {
    // when
    val clazz = TestSimpleModel::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertNotNull(result)
    assertEquals(3, result.count())
    assertTrue { result.containsKey(clazz.simpleName) }
  }

  @Test
  fun `generation works for nested object types`() {
    // when
    val clazz = TestNestedModel::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertNotNull(result)
    assertEquals(4, result.count())
    assertTrue { result.containsKey(clazz.simpleName) }
    assertTrue { result.containsKey(TestSimpleModel::class.simpleName) }
  }

  @Test
  fun `generation does not repeat for cached items`() {
    // when
    val clazz = TestNestedModel::class
    val initialCache = generateKontent(clazz)
    val claxx = TestSimpleModel::class

    // do
    val result = generateKontent(claxx, initialCache)

    // expect TODO Spy to check invocation count?
    assertNotNull(result)
    assertEquals(4, result.count())
    assertTrue { result.containsKey(clazz.simpleName) }
    assertTrue { result.containsKey(TestSimpleModel::class.simpleName) }
  }

  @Test
  fun `generation allows for enum fields`() {
    // when
    val clazz = TestSimpleWithEnums::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertNotNull(result)
    assertEquals(3, result.count())
    assertTrue { result.containsKey(clazz.simpleName) }
  }

  @Test
  fun `generation allows for map fields`() {
    // when
    val clazz = TestSimpleWithMap::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertNotNull(result)
    assertEquals(5, result.count())
    assertTrue { result.containsKey("Map-String-TestSimpleModel") }
    assertTrue { result.containsKey(clazz.simpleName) }

    val os = result[clazz.simpleName] as ObjectSchema
    val expectedRef = ReferencedSchema("#/components/schemas/Map-String-TestSimpleModel")
    assertEquals(expectedRef, os.properties["b"])
  }

  @Test
  fun `map fields that are not string result in error`() {
    // when
    val clazz = TestInvalidMap::class

    // expect
    assertFailsWith<IllegalStateException> { generateKontent(clazz) }
  }

  @Test
  fun `generation allows for collection fields`() {
    // when
    val clazz = TestSimpleWithList::class

    // do
    val result = generateKontent(clazz)

    // expect
    assertNotNull(result)
    assertEquals(6, result.count())
    assertTrue { result.containsKey("List-TestSimpleModel") }
    assertTrue { result.containsKey(clazz.simpleName) }
  }

}
