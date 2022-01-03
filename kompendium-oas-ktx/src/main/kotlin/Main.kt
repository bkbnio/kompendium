import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import oas.OpenApiSpec
import oas.path.Path
import oas.path.PathOperation
import oas.payload.MediaType
import oas.payload.Parameter
import oas.payload.Request
import oas.schema.ComponentSchema
import oas.schema.FormattedSchema
import oas.schema.SimpleSchema
import oas.security.ApiKeyAuth
import oas.security.BasicAuth
import oas.security.BearerAuth
import oas.security.OAuth
import oas.security.SecuritySchema

@Suppress("MagicNumber")
@OptIn(InternalSerializationApi::class)
fun main() {

  val testerino = SerializersModule {
    polymorphic(ComponentSchema::class) {
      subclass(SimpleSchema::class, SimpleSchema.serializer())
      subclass(FormattedSchema::class, FormattedSchema.serializer())
    }
    polymorphic(SecuritySchema::class) {
      subclass(ApiKeyAuth::class, ApiKeyAuth.serializer())
      subclass(BasicAuth::class, BasicAuth.serializer())
      subclass(BearerAuth::class, BearerAuth.serializer())
      subclass(OAuth::class, OAuth.serializer())
    }
    contextual(Any::class, AnySerializer())
  }

  val json = Json {
    classDiscriminator = "class"
    serializersModule = testerino
    prettyPrint = true
    explicitNulls = false
    encodeDefaults = true
  }

  val spec = OpenApiSpec(
    paths = mutableMapOf(
      "/hehe" to Path(
        get = PathOperation(
          parameters = listOf(
            Parameter(
              "testerino",
              `in` = "query",
              SimpleSchema("string"),
              examples = mapOf("test" to Parameter.Example("hi"))
            ),
            Parameter(
              "testerina",
              `in` = "path",
              FormattedSchema("int32", "integer"),
              examples = mapOf("testy" to Parameter.Example(100))
            )
          ),
          requestBody = Request(
            description = "heyo",
            content = mapOf("application/json" to MediaType(
              schema = SimpleSchema("string"), // TODO
              examples = mapOf("200" to MediaType.Example(Testerino("hi", true)))
            ))
          )
        )
      )
    )
  )

  println(json.encodeToString(spec))
}

class AnySerializer<T : Any> : KSerializer<T> {
  override fun serialize(encoder: Encoder, value: T) {
    serialize(encoder, value, value::class as KClass<T>)
  }

  override fun deserialize(decoder: Decoder): T {
    error("Abandon all hope ye who enter 💀")
  }

  override val descriptor: SerialDescriptor
    get() = TODO("Not yet implemented")

  @OptIn(InternalSerializationApi::class)
  fun serialize(encoder: Encoder, obj: T, clazz: KClass<T>) {
    clazz.serializer().serialize(encoder, obj)
  }
}

@Serializable
data class Testerino(val a: String, val b: Boolean)
