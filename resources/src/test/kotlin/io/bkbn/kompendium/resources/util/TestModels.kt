import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)

@Serializable
@Resource("/type/{name}")
data class Type(val name: String) {
  @Serializable
  @Resource("/edit")
  data class Edit(val parent: Type)
  @Serializable
  @Resource("/other/{page}")
  data class Other(val parent: Type, val page: Int)
}
