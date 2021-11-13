package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.metadata.MethodInfo.DeleteInfo
import io.bkbn.kompendium.core.metadata.MethodInfo.GetInfo
import io.bkbn.kompendium.core.metadata.MethodInfo.PostInfo
import io.bkbn.kompendium.core.metadata.MethodInfo.PutInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.ktor.http.HttpStatusCode

object TestResponseInfo {
  private val testGetResponse = ResponseInfo<TestResponse>(HttpStatusCode.OK, "A Successful Endeavor")
  private val testGetListResponse =
    ResponseInfo<List<TestResponse>>(HttpStatusCode.OK, "A Successful List-y Endeavor")
  private val testPostResponse = ResponseInfo<TestCreatedResponse>(HttpStatusCode.Created, "A Successful Endeavor")
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
  val testGetWithException = testGetInfo.copy(
    canThrow = setOf(Exception::class)
  )
  val testGetWithMultipleExceptions = testGetInfo.copy(
    canThrow = setOf(AccessDeniedException::class, Exception::class)
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
  val emptyTestGetInfo =
    GetInfo<OptionalParams, Unit>(
      summary = "No request params and response body",
      description = "testing more"
    )
  val trulyEmptyTestGetInfo = GetInfo<Unit, Unit>(
    summary = "No request params and response body",
    description = "testing more"
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
  val genericResponse = GetInfo<Unit, TestGeneric<Int>>(
    summary = "Single Generic",
    description = "Simple generic data class",
    responseInfo = simpleOkResponse()
  )

  private fun <T> simpleOkResponse() = ResponseInfo<T>(HttpStatusCode.OK, "A successful endeavor")
}
