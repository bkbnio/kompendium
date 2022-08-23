package io.bkbn.kompendium.playground

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.SerializationFeature
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
  }
  install(NotarizedApplication()) {
    spec = baseSpec
    schemaConfigurator = JacksonSchemaConfigurator()
  }
  routing {
    redoc(pageTitle = "Simple API Docs")

    route("/{id}") {
      locationDocumentation()
      get {
        call.respond(HttpStatusCode.OK, ExampleResponse(true))
      }
    }
  }
}

private fun Route.locationDocumentation() {
  install(NotarizedRoute()) {
    parameters = listOf(
      Parameter(
        name = "id",
        `in` = Parameter.Location.path,
        schema = TypeDefinition.STRING
      )
    )
    get = GetInfo.builder {
      summary("Get user by id")
      description("A very neat endpoint!")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<ExampleResponse>()
        description("Will return whether or not the user is real 😱")
      }
    }
  }
}

// Adds support for JsonIgnore and JsonProperty annotations,
// if you are not using them this is not required
// This also does not support class level configuration
private class JacksonSchemaConfigurator: SchemaConfigurator.Default() {

  override fun serializableMemberProperties(clazz: KClass<*>): Collection<KProperty1<out Any, *>> =
    clazz.memberProperties
      .filterNot {
        it.hasJavaAnnotation<JsonIgnore>()
      }

  override fun serializableName(property: KProperty1<out Any, *>): String =
    property.getJavaAnnotation<JsonProperty>()?.value?: property.name

}

private inline fun <reified T: Annotation> KProperty1<*, *>.hasJavaAnnotation(): Boolean {
  return javaField?.isAnnotationPresent(T::class.java)?: false
}

private inline fun <reified T: Annotation> KProperty1<*, *>.getJavaAnnotation(): T? {
  return javaField?.getDeclaredAnnotation(T::class.java)
}
