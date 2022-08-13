rootProject.name = "kompendium"

include("core")
include("oas")
include("auth")
include("playground")
include("locations")
include("json-schema")

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
