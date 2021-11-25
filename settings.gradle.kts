rootProject.name = "kompendium"

include("kompendium-core")
include("kompendium-auth")
include("kompendium-swagger-ui")
include("kompendium-playground")
include("kompendium-locations")

// Feature Previews
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")
