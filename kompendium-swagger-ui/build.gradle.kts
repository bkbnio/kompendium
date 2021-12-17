plugins {
  id("io.bkbn.sourdough.library")
}

dependencies {
  api(libs.ktor.webjars)
  implementation(libs.bundles.ktor)
  implementation(libs.webjars.swagger.ui)
}
