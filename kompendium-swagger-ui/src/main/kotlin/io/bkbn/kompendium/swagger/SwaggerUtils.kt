package io.bkbn.kompendium.swagger

import io.ktor.application.ApplicationCall
import io.ktor.features.NotFoundException
import io.ktor.http.content.ByteArrayContent
import io.ktor.util.pipeline.PipelineContext
import java.net.URI
import java.net.URL

fun PipelineContext<*, ApplicationCall>.getWebJarResource(path: String): URL? =
    this::class.java.getResource("/META-INF/resources/webjars/$path")

fun PipelineContext<*, ApplicationCall>.getSwaggerResource(swaggerVersion: String, filePath: String): URL? =
    getWebJarResource("swagger-ui/$swaggerVersion/$filePath")

fun PipelineContext<*, ApplicationCall>.getSwaggerResourceContent(
    swaggerVersion: String,
    filePath: String
): ByteArrayContent =
    getSwaggerResource(swaggerVersion = swaggerVersion, filePath = filePath)?.readBytes()?.let {
        ByteArrayContent(it)
    } ?: throw NotFoundException("Resource not found: $filePath")

private fun docUrlEntry(name: String, url: String): String =
        """
            {
              "url": "$url",
              "name": "$name"
            }
        """

private fun Map<String, URI>.buildDocUrls(): String =
    if (isEmpty()) "[]" else map { docUrlEntry(it.key, it.value.toString()) }.toString()

fun PipelineContext<*, ApplicationCall>.getSwaggerIndexContent(
    swaggerVersion: String,
    specs: Map<String, URI>,
    jsInit: () -> String?
): ByteArrayContent =
    getSwaggerResource(swaggerVersion = swaggerVersion, filePath = "index.html")?.let { url ->
        ByteArrayContent(
            url.readText()
                .replaceFirst("url: \"https://petstore.swagger.io/v2/swagger.json\",", "urls: ${specs.buildDocUrls()},")
                .replaceFirst("deepLinking: true,",
    """
        deepLinking: true,
        displayOperationId: true,
        displayRequestDuration: true,
        docExpansion: 'none',
        operationsSorter: 'alpha',
        defaultModelExpandDepth: 4,
        defaultModelsExpandDepth: 4,
        persistAuthorization: true,
        tagsSorter: 'alpha',
        tryItOutEnabled: false,
        validatorUrl: null,
    """
                )
                .let { content ->
                    jsInit()?.let {
                        content.replaceFirst("window.ui = ui", "$it\n\twindow.ui = ui")
                    } ?: content
                }
                .toByteArray()
        )
    } ?: throw NotFoundException("Resource not found: index.html")
