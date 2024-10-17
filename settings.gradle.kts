rootProject.name = "kompendium"

include("core")
include("enrichment")
include("oas")
include("playground")
include("json-schema")
include("protobuf-java-converter")
include("resources")

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
