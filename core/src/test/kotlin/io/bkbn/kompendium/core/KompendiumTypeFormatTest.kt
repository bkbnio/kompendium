package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.dateTimeString
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.kotest.core.spec.style.DescribeSpec
import java.time.Instant
import kotlin.reflect.typeOf

class KompendiumTypeFormatTest : DescribeSpec({
  describe("Formats") {
    it("Can set a format for a simple type schema") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0038__formatted_date_time_string.json",
        customTypes = mapOf(typeOf<Instant>() to TypeDefinition(type = "string", format = "date"))
      ) { dateTimeString() }
    }
  }
})
