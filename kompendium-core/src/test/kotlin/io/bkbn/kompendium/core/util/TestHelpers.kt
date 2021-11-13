package io.bkbn.kompendium.core.util

import java.io.File

object TestHelpers {
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
