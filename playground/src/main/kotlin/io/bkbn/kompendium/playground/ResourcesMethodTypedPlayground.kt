package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.bkbn.kompendium.resources.NotarizedDeleteResource
import io.bkbn.kompendium.resources.NotarizedGetResource
import io.bkbn.kompendium.resources.NotarizedHeadResource
import io.bkbn.kompendium.resources.NotarizedOptionsResource
import io.bkbn.kompendium.resources.NotarizedPatchResource
import io.bkbn.kompendium.resources.NotarizedPostResource
import io.bkbn.kompendium.resources.NotarizedPutResource
import io.bkbn.kompendium.resources.NotarizedResource
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.head
import io.ktor.server.resources.options
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(Resources)
  install(ContentNegotiation) {
    json(Json {
      serializersModule = KompendiumSerializersModule.module
      encodeDefaults = true
      explicitNulls = false
    })
  }
  install(NotarizedApplication()) {
    spec = { baseSpec }
  }
  routing {
    swagger(pageTitle = "Simple API Docs")
    redoc(pageTitle = "Simple API Docs")

    listUserRoute()
    createUserRoute()
    userMetadataRoute()

    getUserRoute()
    updateUserRoute()
    updateUserPartialRoute()
    deleteUserRoute()
    getUserMetadataRoute()

    getUserAssignedRolesRoute()
    getUserAssignedRolesOptionsRoute()
  }
}

@Resource("/users")
class Users {
  @Resource("{id}")
  data class Id(val parent: Users = Users(), val id: Long) {
    @Resource("/roles")
    data class Roles(val parent: Id)
  }
}

fun Route.listUserRoute() {
  listUserDocumentation()

  get<Users> {
    call.respondText("List user")
  }
}

private fun Route.listUserDocumentation() {
  install(NotarizedGetResource<Users>()) {
    get = GetInfo.builder {
      summary("List users")
      description("List all users")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("List of users")
      }
    }
  }
}

fun Route.createUserRoute() {
  createUserDocumentation()

  post<Users> {
    call.respondText("Successfully created user", status = HttpStatusCode.Created)
  }
}

private fun Route.createUserDocumentation() {
  install(NotarizedPostResource<Users>()) {
    post = PostInfo.builder {
      summary("Create user")
      description("Create a new user")
      response {
        responseCode(HttpStatusCode.Created)
        responseType<String>()
        description("User created")
      }
    }
  }
}

fun Route.userMetadataRoute() {
  userMetadataDocumentation()

  head<Users> {
    call.respondText("List users metadata")
  }
}

private fun Route.userMetadataDocumentation() {
  install(NotarizedHeadResource<Users>()) {
    head = HeadInfo.builder {
      summary("List users metadata")
      description("List metadata via headers")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("List of users metadata")
      }
    }
  }
}

fun Route.getUserRoute() {
  getUserDocumentation()

  get<Users.Id> {
    call.respondText("Get user")
  }
}

private fun Route.getUserDocumentation() {
  install(NotarizedGetResource<Users.Id>()) {
    get = GetInfo.builder {
      summary("Get user")
      description("Get a user by ID")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("User found")
      }
    }
  }
}

fun Route.updateUserRoute() {
  updateUserDocumentation()

  put<Users.Id> {
    call.respondText("User updated")
  }
}

private fun Route.updateUserDocumentation() {
  install(NotarizedPutResource<Users.Id>()) {
    put = PutInfo.builder {
      summary("Update user")
      description("Update a user by ID")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("User updated")
      }
    }
  }
}

fun Route.updateUserPartialRoute() {
  updateUserPartialDocumentation()

  patch<Users.Id> {
    call.respondText("User fields updated", status = HttpStatusCode.NoContent)
  }
}

private fun Route.updateUserPartialDocumentation() {
  install(NotarizedPatchResource<Users.Id>()) {
    patch = PatchInfo.builder {
      summary("Update specific user fields")
      description("Update specific fields of a user by ID")
      response {
        responseCode(HttpStatusCode.NoContent)
        responseType<String>()
        description("User fields updated")
      }
    }
  }
}

fun Route.deleteUserRoute() {
  deleteUserDocumentation()

  delete<Users> {
    call.respondText("User deleted", status = HttpStatusCode.NoContent)
  }
}

private fun Route.deleteUserDocumentation() {
  install(NotarizedDeleteResource<Users>()) {
    delete = DeleteInfo.builder {
      summary("Delete user")
      description("Delete a user by ID")
      response {
        responseCode(HttpStatusCode.NoContent)
        responseType<String>()
        description("User deleted")
      }
    }
  }
}

fun Route.getUserMetadataRoute() {
  getUserMetadataDocumentation()

  head<Users.Id> {
    call.respondText("Get user metadata")
  }
}

private fun Route.getUserMetadataDocumentation() {
  install(NotarizedHeadResource<Users.Id>()) {
    head = HeadInfo.builder {
      summary("Get user metadata")
      description("Get metadata for a user by ID")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("User metadata found")
      }
    }
  }
}

fun Route.getUserAssignedRolesRoute() {
  getUserAssignedRolesDocumentation()

  get<Users.Id.Roles> {
    call.respondText("Get user assigned roles")
  }
}

private fun Route.getUserAssignedRolesDocumentation() {
  install(NotarizedResource<Users.Id.Roles>()) {
    get = GetInfo.builder {
      summary("Get user assigned roles")
      description("Get roles assigned to a user by ID")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("User assigned roles")
      }
    }
  }
}

fun Route.getUserAssignedRolesOptionsRoute() {
  getUserAssignedRolesOptionsDocumentation()

  options<Users.Id.Roles> {
    call.respondText("Get user assigned roles")
  }
}

private fun Route.getUserAssignedRolesOptionsDocumentation() {
  install(NotarizedOptionsResource<Users.Id.Roles>()) {
    options = OptionsInfo.builder {
      summary("Get options for this endpoint")
      description("Get options for this endpoint")
      response {
        responseCode(HttpStatusCode.OK)
        responseType<String>()
        description("Test the allowed HTTP methods for this endpoint")
      }
    }
  }
}
