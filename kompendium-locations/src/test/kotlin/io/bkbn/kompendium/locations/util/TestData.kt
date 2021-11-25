package io.bkbn.kompendium.locations.util

import java.io.File

object TestData {
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
