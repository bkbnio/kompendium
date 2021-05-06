package org.leafygreens.kompendium.util

import io.ktor.http.HttpStatusCode
import org.leafygreens.kompendium.models.meta.MethodInfo
import org.leafygreens.kompendium.models.meta.RequestInfo
import org.leafygreens.kompendium.models.meta.ResponseInfo

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
  val testGetInfo = MethodInfo.GetInfo<TestParams, TestResponse>(
    summary = "Another get test",
    description = "testing more",
    responseInfo = testGetResponse
  )
  val testGetInfoAgain = MethodInfo.GetInfo<TestParams, List<TestResponse>>(
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
  val testPostInfo = MethodInfo.PostInfo<TestParams, TestRequest, TestCreatedResponse>(
    summary = "Test post endpoint",
    description = "Post your tests here!",
    responseInfo = testPostResponse,
    requestInfo = testRequest
  )
  val testPutInfo = MethodInfo.PutInfo<Unit, ComplexRequest, TestCreatedResponse>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponse,
    requestInfo = complexRequest
  )
  val testPutInfoAlso = MethodInfo.PutInfo<TestParams, TestRequest, TestCreatedResponse>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponse,
    requestInfo = testRequest
  )
  val testPutInfoAgain = MethodInfo.PutInfo<Unit, Int, Boolean>(
    summary = "Test put endpoint",
    description = "Put your tests here!",
    responseInfo = testPostResponseAgain,
    requestInfo = testRequestAgain
  )
  val testDeleteInfo = MethodInfo.DeleteInfo<TestParams, Unit>(
    summary = "Test delete endpoint",
    description = "testing my deletes",
    responseInfo = testDeleteResponse
  )
  val emptyTestGetInfo =
    MethodInfo.GetInfo<OptionalParams, Unit>(
      summary = "No request params and response body",
      description = "testing more"
    )
  val trulyEmptyTestGetInfo =
    MethodInfo.GetInfo<Unit, Unit>(summary = "No request params and response body", description = "testing more")
}
