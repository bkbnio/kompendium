The `Protobuf java converter` functions allow you to generate documentation from your generated Java classes.
Since Kompendium relies a lot on `KProperties` we have yet to find a way to connect this with our Java.
For now the documentation is generated for the `customTypes` in `NotarizedApplication`.

## Usage with Kotlinx

setup:
```kotlin
 install(ContentNegotiation) {
  json(Json {
    encodeDefaults = false
    // Combine the kompendium serializers with your custom java proto serializers 
    serializersModule =
      KompendiumSerializersModule.module + SerializersModule { serializersModule = yourCustomProtoSerializers }
  })
}
```

For one message and all its nested sub messages:
```kotlin
private fun Application.mainModule() {
  // ...
  install(NotarizedApplication()) {
    spec = baseSpec
    customTypes = MyJavaProto.getDefaultInstance().createCustomTypesForTypeAndSubTypes().toMap()
  }
}
```

For multiple messages and their submesages:
```kotlin
private fun Application.mainModule() {
  // ...
  install(NotarizedApplication()) {
    spec = baseSpec
    customTypes = MyJavaProto.getDefaultInstance().createCustomTypesForTypeAndSubTypes()
      .plus(AnotherJavaProto.getDefaultInstance().createCustomTypesForTypeAndSubTypes()).toMap()
  }
}
```

### Example User

The protobuf that is used on our endpoint
```proto
message User {
  string id = 1;
  string email = 2;
  string mobile_phone = 3;
  string name = 4;
}
```

A custom serializer deserializer:
```kotlin
@OptIn(ExperimentalSerializationApi::class)
object UserSerializer : KSerializer<User> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("User") {
    element("id", serialDescriptor<String>())
    element("email", serialDescriptor<String>())
    element("mobile_phone", serialDescriptor<String>())
    element("name", serialDescriptor<String>())
  }

  override fun deserialize(decoder: Decoder): User {
    return decoder.decodeStructure(descriptor) {
      var id: String? = null
      var email: String? = null
      var mobilePhone: String? = null
      var name: String? = null

      loop@ while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          CompositeDecoder.DECODE_DONE -> break@loop
          0 -> id = decodeStringElement(descriptor, index)
          1 -> email = decodeStringElement(descriptor, index)
          2 -> mobilePhone = decodeStringElement(descriptor, index)
          3 -> name = decodeStringElement(descriptor, index)
          else -> throw   RuntimeException(
            "Unexpected index field ${descriptor.getElementName(index)}"
          )
        }
      }
      // building the protobuf object
      val user = User.newBuilder().apply {
        id?.let { v -> this.id = v }
        email?.let { v -> this.email = v }
        mobilePhone?.let { v -> this.mobilePhone = v }
        name?.let { v -> this.name = v }
      }.build()
      user
    }
  }

  override fun serialize(encoder: Encoder, value: User) {
    encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value.id)
      encodeStringElement(descriptor, 1, value.email)
      encodeStringElement(descriptor, 2, value.mobilePhone)
      encodeStringElement(descriptor, 3, value.name)
    }
  }
}
```
Setting the content type:
```kotlin
install(ContentNegotiation) {
  json(Json {
    encodeDefaults = false
    // Combine the kompendium serializers with your custom java proto serializers 
    serializersModule =
      KompendiumSerializersModule.module + SerializersModule { 
        serializersModule = SerializersModule {
          contextual(UserSerializer)
        }
      }
  })
}
```
The installation of the noterized application:
```kotlin
install(NotarizedApplication()) {
  spec = baseSpec
  customTypes = User.getDefaultInstance().createCustomTypesForTypeAndSubTypes().toMap()
}
```
Route configuration as you normally would with one exception which is `createType()` to create kotlin type from a javaClass.

```kotlin
private fun Route.userDocumentation() {
  install(NotarizedRoute()) {
    post = PostInfo.builder {
      summary("My User API")
      description("Create a user")
      request {
        requestType(User::class.createType())
        description("My user creation object")
      }
      response {
        responseCode(HttpStatusCode.OK)
        responseType(CreateUserResponse::class.createType())
        description("Returns simulation object")
      }
      canRespond {
        responseCode(HttpStatusCode.NotFound)
        responseType<String>()
        description("Indicates that the user could not be found")
      }
    }
  }
}
```
