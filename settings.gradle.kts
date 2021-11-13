rootProject.name = "kompendium"

include("kompendium-core")
include("kompendium-oas")
include("kompendium-auth")
include("kompendium-swagger-ui")
include("kompendium-playground")

// Feature Previews
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")
