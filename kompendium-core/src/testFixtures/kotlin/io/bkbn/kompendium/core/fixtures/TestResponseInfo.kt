package io.bkbn.kompendium.core.fixtures

import io.bkbn.kompendium.core.metadata.ExceptionInfo
import io.bkbn.kompendium.core.metadata.ParameterExample
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.DeleteInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.HeadInfo
import io.bkbn.kompendium.core.metadata.method.OptionsInfo
import io.bkbn.kompendium.core.metadata.method.PatchInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.metadata.method.PutInfo
import io.ktor.http.HttpStatusCode
import kotlin.reflect.typeOf

object TestResponseInfo {
  val testGetResponse = ResponseInfo<TestResponse>(HttpStatusCode.OK, "A Successful Endeavor")
  private val testGetListResponse =
    ResponseInfo<List<TestResponse>>(HttpStatusCode.OK, "A Successful List-y Endeavor")
  private val testPostResponse = ResponseInfo<TestCreatedResponse>(HttpStatusCode.Created, "A Successful Endeavor")
  private val testPatchResponse = ResponseInfo<TestResponse>(HttpStatusCode.Created, "A Successful Endeavor")
  private val testPostResponseAgain = ResponseInfo<Boolean>(HttpStatusCode.Created, "A Successful Endeavor")
  private val testDeleteResponse =
    ResponseInfo<Unit>(HttpStatusCode.NoContent, "A Successful Endeavor", mediaTypes = emptyList())
  private val testRequest = RequestInfo<TestRequest>("A Test request")
  private val testRequestAgain = RequestInfo<Int>("A Test request")
  private val complexRequest = RequestInfo<ComplexRequest>("A Complex request")
  val testGetInfo = GetInfo<TestParams, TestResponse>(
    summary = "Another get test",
    description = "testing more",
    responseInfo = testGetResponse
  )
  val testGetInfoAgain = GetInfo<TestParams, List<TestResponse>>(
    summary = "Another get test",
    description = "testing more",
    responseInfo = testGetListResponse
  )
  private val accessDeniedResponse = ExceptionInfo<ExceptionResponse>(
    responseType = typeOf<ExceptionResponse>(),
    description = "Access Denied",
    status = HttpStatusCode.Forbidden
  )
  private val polymorphicException = ExceptionInfo<FlibbityGibbit>(
    responseType = typeOf<FlibbityGibbit>(),
    description = "The Gibbits are ANGRY",
    status = HttpStatusCode.NotImplemented
  )
  private val genericException = ExceptionInfo<Flibbity<String>>(
    responseType = typeOf<Flibbity<String>>(),
    description = "Wow serious things went wrong",
    status = HttpStatusCode.BadRequest
  )
  private val exceptionResponseInfo = ExceptionInfo<ExceptionResponse>(
    responseType = typeOf<ExceptionResponse>(),
    description = "Bad Things Happened",
    status = HttpStatusCode.BadRequest
  )
  val testGetWithException = testGetInfo.copy(
    canThrow = setOf(exceptionResponseInfo)
  )
  val testGetWithMultipleExceptions = testGetInfo.copy(
    canThrow = setOf(accessDeniedResponse, exceptionResponseInfo)
  )
  val testGetWithPolymorphicException = testGetInfo.copy(
    canThrow = setOf(polymorphicException)
  )
  val testGetWithGenericException = testGetInfo.copy(
    canThrow = setOf(genericException)
  )
  val testPostInfo = PostInfo<TestParams, TestRequest, TestCreatedResponse>(
    summary = "Test post endpoint",
    description = "Post your tests here!",
    responseInfo = testPostResponse,
    requestInfo = testRequest
  )
  val testPutInfo = PutInfo<Unit, ComplexRequest, TestCreatedResponse>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponse,
    requestInfo = complexRequest
  )
  val testPatchInfo = PatchInfo<Unit, TestRequest, TestResponse>(
    summary = "Test patch endpoint",
    description = "patch your tests here!",
    responseInfo = testPatchResponse,
    requestInfo = testRequest
  )
  val testHeadInfo = HeadInfo<Unit>(
    summary = "Test head endpoint",
    description = "head test 💀",
    responseInfo = ResponseInfo(HttpStatusCode.OK, "great!")
  )
  val testOptionsInfo = OptionsInfo<TestParams, TestResponse>(
    summary = "Test options",
    description = "endpoint of options",
    responseInfo = ResponseInfo(HttpStatusCode.OK, "nice")
  )
  val testPutInfoAlso = PutInfo<TestParams, TestRequest, TestCreatedResponse>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponse,
    requestInfo = testRequest
  )
  val testPutInfoAgain = PutInfo<Unit, Int, Boolean>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponseAgain,
    requestInfo = testRequestAgain
  )
  val testDeleteInfo = DeleteInfo<TestParams, Unit>(
    summary = "Test delete endpoint",
    description = "testing my deletes",
    responseInfo = testDeleteResponse
  )
  val testOptionalParams = GetInfo<OptionalParams, Unit>(
    summary = "No request params and response body",
    description = "testing more",
    responseInfo = ResponseInfo(HttpStatusCode.NoContent, "Empty")
  )
  val polymorphicResponse = GetInfo<Unit, FlibbityGibbit>(
    summary = "All the gibbits",
    description = "Polymorphic response",
    responseInfo = simpleOkResponse()
  )
  val polymorphicListResponse = GetInfo<Unit, List<FlibbityGibbit>>(
    summary = "Oh so many gibbits",
    description = "Polymorphic list response",
    responseInfo = simpleOkResponse()
  )
  val polymorphicMapResponse = GetInfo<Unit, Map<String, FlibbityGibbit>>(
    summary = "By gawd that's a lot of gibbits",
    description = "Polymorphic list response",
    responseInfo = simpleOkResponse()
  )
  val polymorphicInterfaceResponse = GetInfo<Unit, SlammaJamma>(
    summary = "Come on and slam",
    description = "and welcome to the jam",
    responseInfo = simpleOkResponse()
  )
  val genericPolymorphicResponse = GetInfo<Unit, Flibbity<TestNested>>(
    summary = "More flibbity",
    description = "Polymorphic with generics",
    responseInfo = simpleOkResponse()
  )
  val anotherGenericPolymorphicResponse = GetInfo<Unit, Flibbity<FlibbityGibbit>>(
    summary = "The Most Flibbity",
    description = "Polymorphic with generics but like... crazier",
    responseInfo = simpleOkResponse()
  )
  val undeclaredResponseType = GetInfo<Unit, Mysterious>(
    summary = "spooky class",
    description = "break this glass in scenario of emergency",
    responseInfo = simpleOkResponse()
  )
  val headerParam = GetInfo<HeaderNameTest, TestResponse>(
    summary = "testing header stuffs",
    description = "Good for many things",
    responseInfo = simpleOkResponse()
  )
  val genericResponse = GetInfo<Unit, TestGeneric<Int>>(
    summary = "Single Generic",
    description = "Simple generic data class",
    responseInfo = simpleOkResponse()
  )
  val fieldOverride = GetInfo<Unit, TestFieldOverride>(
    summary = "A Response with a spicy field",
    description = "Important info within!",
    responseInfo = simpleOkResponse()
  )

  val simpleRecursive = GetInfo<Unit, ColumnSchema>(
    summary = "Simple recursive example",
    description = "Pretty neato!",
    responseInfo = simpleOkResponse()
  )

  val nullableNested = PostInfo<Unit, ProfileUpdateRequest, TestResponse>(
    summary = "Has a bunch of nullable fields",
    description = "Should still work!",
    requestInfo = RequestInfo(
      description = "Cool"
    ),
    responseInfo = simpleOkResponse()
  )

  val minMaxInt = GetInfo<Unit, MinMaxInt>(
    summary = "Constrained int field",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val minMaxDouble = GetInfo<Unit, MinMaxDouble>(
    summary = "Constrained int field",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val exclusiveMinMax = GetInfo<Unit, ExclusiveMinMax>(
    summary = "Constrained int field",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val requiredParam = GetInfo<RequiredParam, TestResponse>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val formattedParam = GetInfo<FormattedString, TestResponse>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val minMaxString = GetInfo<Unit, MinMaxString>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val regexString = GetInfo<Unit, RegexString>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val minMaxArray = GetInfo<Unit, MinMaxArray>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val uniqueArray = GetInfo<Unit, UniqueArray>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val multipleOfInt = GetInfo<Unit, MultipleOfInt>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val multipleOfDouble = GetInfo<Unit, MultipleOfDouble>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val freeFormObject = GetInfo<Unit, FreeFormData>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val minMaxFreeForm = GetInfo<Unit, MinMaxFreeForm>(
    summary = "required param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val defaultParam = GetInfo<DefaultParam, TestResponse>(
    summary = "default param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse()
  )

  val defaultField = PostInfo<Unit, DefaultField, TestResponse>(
    summary = "default param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse(),
    requestInfo = RequestInfo("cool")
  )

  val nullableField = PostInfo<Unit, NullableField, TestResponse>(
    summary = "default param",
    description = "Cool stuff",
    responseInfo = simpleOkResponse(),
    requestInfo = RequestInfo("cool")
  )

  val exampleParams = GetInfo<TestParams, TestResponse>(
    summary = "param stuff",
    description = "Cool stuff",
    responseInfo = simpleOkResponse(),
    parameterExamples = setOf(
      ParameterExample(TestParams::a.name, "Testerino", "a"),
      ParameterExample(TestParams::a.name, "Testerina", "b"),
      ParameterExample(TestParams::aa.name, "Wowza", 6),
    )
  )

  val formattedArrayItemType = PostInfo<Unit, FormattedArrayItemType, TestResponse>(
      summary = "formatted array item type",
      description = "Cool stuff",
      responseInfo = simpleOkResponse(),
      requestInfo = RequestInfo("cool")
  )

  private fun <T> simpleOkResponse() = ResponseInfo<T>(HttpStatusCode.OK, "A successful endeavor")
}
