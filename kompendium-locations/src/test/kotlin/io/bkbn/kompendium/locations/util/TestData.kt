package io.bkbn.kompendium.locations.util

import io.bkbn.kompendium.core.Kompendium
import java.io.File

object TestData {
  const val OPEN_API_ENDPOINT = "/openapi.json"
  fun oas() = Kompendium.openApiSpec.copy()
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
