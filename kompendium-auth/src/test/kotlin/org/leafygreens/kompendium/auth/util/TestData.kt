package org.leafygreens.kompendium.auth.util

import java.io.File

object TestData {
  object AuthConfigName {
    val Basic = "basic"
    val JWT = "jwt"
  }

  val getRoutePath = "/test"

  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
