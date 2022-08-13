import io.ktor.server.locations.Location

@Location("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)

@Location("/type/{name}")
data class Type(val name: String) {
  @Location("/edit")
  data class Edit(val parent: Type)
  @Location("/other/{page}")
  data class Other(val parent: Type, val page: Int)
}
