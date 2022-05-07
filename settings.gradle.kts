rootProject.name = "kompendium"

include("annotations")
include("core")
include("core2") // FIXME will become core once implemented
include("oas")
include("auth")
include("playground")
include("locations")

run {
  rootProject.children.forEach { it.name = "${rootProject.name}-${it.name}" }
}

// Feature Previews
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}
