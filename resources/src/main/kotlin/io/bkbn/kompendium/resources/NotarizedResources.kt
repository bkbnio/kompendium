package io.bkbn.kompendium.resources

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
import kotlin.reflect.KClass

object NotarizedResources {

  data class ResourceMetadata(
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
    lateinit var resources: Map<KClass<*>, ResourceMetadata>
  }

  operator fun invoke() = createApplicationPlugin(
    name = "NotarizedResources",
    createConfiguration = NotarizedResources::Config
  ) {
    val spec = application.attributes[KompendiumAttributes.openApiSpec]
    val serializableReader = application.attributes[KompendiumAttributes.schemaConfigurator]
    pluginConfig.resources.forEach { (k, v) ->
      val resource = k.getResourcePathFromClass()
      val path = spec.paths[resource] ?: Path()
      path.parameters = path.parameters?.plus(v.parameters) ?: v.parameters
      v.get?.addToSpec(path, spec, v, serializableReader, resource)
      v.delete?.addToSpec(path, spec, v, serializableReader, resource)
      v.head?.addToSpec(path, spec, v, serializableReader, resource)
      v.options?.addToSpec(path, spec, v, serializableReader, resource)
      v.post?.addToSpec(path, spec, v, serializableReader, resource)
      v.put?.addToSpec(path, spec, v, serializableReader, resource)
      v.patch?.addToSpec(path, spec, v, serializableReader, resource)

      spec.paths[resource] = path
    }
  }
}
