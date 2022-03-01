# Module kompendium-swagger-ui

This module is responsible for frontend part of `SwaggerUI` built on top on WebJar.

Solution is wrapped into KTor plugin that may be tuned with configuration properties according to 
Swagger UI official documentation (`JsConfig` is responsible for that): 

https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/

Current implementation covers only most important part of specification properties (we'll be adding more time to time)  

# Module configuration

Minimal SwaggerUI plugin configuration:

```kotlin
import io.bkbn.kompendium.swagger.JsConfig
import io.bkbn.kompendium.swagger.SwaggerUI
import io.ktor.application.install

install(SwaggerUI) {
  swaggerUrl = "/swagger-ui"
  jsConfig = JsConfig(
    specs = mapOf(
      "Your API name" to URI("/openapi.json")
    )
  )
}

```

Additionally, there is a way to add additional initialization code in SwaggerUI JS.
`JsConfig.jsInit` is responsible for that:

```kotlin
JsConfig(
  //...
  jsInit = {
    """
      ui.initOAuth(...)    
    """
  } 
)
```

# Playground example

There is an example that demonstrates how this plugin is working in `kompendium-playground` module:

```
io.bkbn.kompendium.playground.SwaggerPlayground.kt
``` 