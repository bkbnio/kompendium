package io.bkbn.kompendium.locations

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.util.Helpers.addToSpec
import io.bkbn.kompendium.core.util.SpecConfig
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.payload.Parameter
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Location
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

@Deprecated(
  message = "This functionality is deprecated and will be removed in the future. " +
    "Use 'ktor-server-resources' with 'kompendium-resources' plugin instead.",
  level = DeprecationLevel.WARNING
)
object NotarizedLocations {

  data class LocationMetadata(
    override var tags: Set<String> = emptySet(),
    override var parameters: List<Parameter> = emptyList(),
    override var get: GetInfo? = null,
    override var post: PostInfo? = null,
    override var put: PutInfo? = null,
    override var delete: DeleteInfo? = null,
    override var patch: PatchInfo? = null,
    override var head: HeadInfo? = null,
    override var options: OptionsInfo? = null,
    override var security: Map<String, List<String>>? = null,
  ) : SpecConfig

  class Config {
    lateinit var locations: Map<KClass<*>, LocationMetadata>
  }

  operator fun invoke() = createApplicationPlugin(
    name = "NotarizedLocations",
    createConfiguration = ::Config
  ) {
    val spec = application.attributes[KompendiumAttributes.openApiSpec]
    val serializableReader = application.attributes[KompendiumAttributes.schemaConfigurator]
    pluginConfig.locations.forEach { (k, v) ->
      val path = Path()
      path.parameters = v.parameters
      v.get?.addToSpec(path, spec, v, serializableReader)
      v.delete?.addToSpec(path, spec, v, serializableReader)
      v.head?.addToSpec(path, spec, v, serializableReader)
      v.options?.addToSpec(path, spec, v, serializableReader)
      v.post?.addToSpec(path, spec, v, serializableReader)
      v.put?.addToSpec(path, spec, v, serializableReader)
      v.patch?.addToSpec(path, spec, v, serializableReader)

      val location = k.getLocationFromClass()
      spec.paths[location] = path
    }
  }

  @OptIn(KtorExperimentalLocationsAPI::class)
  private fun KClass<*>.getLocationFromClass(): String {
    // todo if parent

    val location = findAnnotation<Location>()
      ?: error("Cannot notarize a location without annotating with @Location")

    val path = location.path
    val parent = memberProperties.map { it.returnType.classifier as KClass<*> }.find { it.hasAnnotation<Location>() }

    return if (parent == null) {
      path
    } else {
      parent.getLocationFromClass() + path
    }
  }
}
