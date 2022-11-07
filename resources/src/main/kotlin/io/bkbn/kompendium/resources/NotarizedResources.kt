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
import io.ktor.resources.Resource
import io.ktor.server.application.createApplicationPlugin
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

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
      val path = Path()
      path.parameters = v.parameters
      v.get?.addToSpec(path, spec, v, serializableReader)
      v.delete?.addToSpec(path, spec, v, serializableReader)
      v.head?.addToSpec(path, spec, v, serializableReader)
      v.options?.addToSpec(path, spec, v, serializableReader)
      v.post?.addToSpec(path, spec, v, serializableReader)
      v.put?.addToSpec(path, spec, v, serializableReader)
      v.patch?.addToSpec(path, spec, v, serializableReader)

      val resource = k.getResourcesFromClass()
      require(spec.paths[resource] == null) {
        """
          The specified path $resource has already been documented!
          Please make sure that all notarized paths are unique
        """.trimIndent()
      }
      spec.paths[resource] = path
    }
  }

  private fun KClass<*>.getResourcesFromClass(): String {
    // todo if parent

    val resource = findAnnotation<Resource>()
      ?: error("Cannot notarize a resource without annotating with @Resource")

    val path = resource.path
    val parent = memberProperties.map { it.returnType.classifier as KClass<*> }.find { it.hasAnnotation<Resource>() }

    return if (parent == null) {
      path
    } else {
      parent.getResourcesFromClass() + path
    }
  }
}
