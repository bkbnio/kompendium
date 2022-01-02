plugins {
  id("io.bkbn.sourdough.library")
  `java-test-fixtures`
}

dependencies {
  // IMPLEMENTATION

  api(projects.kompendiumOas)
  api(projects.kompendiumAnnotations)

  val ktorVersion: String by project
  val kotestVersion: String by project
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-html-builder", version = ktorVersion)

  implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.13.0")

  // TEST FIXTURES

  testFixturesApi(group = "io.kotest", name = "kotest-runner-junit5-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-core-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-property-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-json-jvm", version = kotestVersion)
  testFixturesApi(group = "io.kotest", name = "kotest-assertions-ktor-jvm", version = "4.4.3")

  testFixturesApi(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-server-test-host", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-jackson", version = ktorVersion)
  testFixturesApi(group = "io.ktor", name = "ktor-serialization", version = ktorVersion)

  testFixturesApi(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.1")
}
