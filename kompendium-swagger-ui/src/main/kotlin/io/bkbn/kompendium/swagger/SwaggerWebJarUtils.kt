package io.bkbn.kompendium.swagger

import io.ktor.features.NotFoundException
import io.ktor.http.content.ByteArrayContent
import java.net.URL
import org.webjars.WebJarAssetLocator

internal fun WebJarAssetLocator.getSwaggerResource(path: String): URL =
  this::class.java.getResource(getFullPath("swagger-ui", path).let { if (it.startsWith("/")) it else "/$it" })
    ?: throw NotFoundException("Resource not found: $path")

internal fun WebJarAssetLocator.getSwaggerResourceContent(path: String): ByteArrayContent =
  ByteArrayContent(getSwaggerResource(path).readBytes())

internal fun WebJarAssetLocator.getSwaggerInitializerContent(jsConfig: JsConfig): ByteArrayContent = ByteArrayContent(
    getSwaggerResource(path = "swagger-initializer.js").readText()
      .replaceFirst("url: \"https://petstore.swagger.io/v2/swagger.json\",", "urls: ${jsConfig.getSpecUrlsProps()},")
      .replaceFirst("deepLinking: true", jsConfig.toJsProps())
      .let { content ->
        jsConfig.jsInit()?.let {
          content.replaceFirst("});", "});\n$it")
        } ?: content
      }.toByteArray()
  )
