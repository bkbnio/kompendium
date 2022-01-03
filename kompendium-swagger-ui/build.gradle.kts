plugins {
  id("io.bkbn.sourdough.library")
}

dependencies {
  val ktorVersion: String by project
  implementation(group = "io.ktor", name = "ktor-server-core", version = ktorVersion)
  implementation(group = "io.ktor", name = "ktor-webjars", version = ktorVersion)
  implementation(group = "org.webjars", name = "swagger-ui", version = "4.1.3")
}
