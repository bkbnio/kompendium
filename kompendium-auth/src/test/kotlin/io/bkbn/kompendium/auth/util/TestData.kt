package io.bkbn.kompendium.auth.util

import java.io.File

object TestData {
  object AuthConfigName {
    const val Basic = "basic"
    const val JWT = "jwt"
  }

  const val getRoutePath = "/test"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
