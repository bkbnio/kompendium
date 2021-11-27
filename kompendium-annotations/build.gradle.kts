plugins {
  id("kotlin-library-conventions")
}

tasks.dokkaHtmlPartial.configure {
  dokkaSourceSets {
    configureEach {
      includes.from("Module.md")
    }
  }
}
