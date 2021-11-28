package io.bkbn.kompendium.locations.util

import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.ktor.http.HttpStatusCode

object TestResponseInfo {
  val testGetSimpleLocation = MethodInfo.GetInfo<SimpleLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPostSimpleLocation = MethodInfo.PostInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    requestInfo = RequestInfo(
      description = "Cool stuff"
    ),
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPutSimpleLocation = MethodInfo.PutInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    requestInfo = RequestInfo(
      description = "Cool stuff"
    ),
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testDeleteSimpleLocation = MethodInfo.DeleteInfo<SimpleLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testGetNestedLocation = MethodInfo.GetInfo<SimpleLoc.NestedLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPostNestedLocation = MethodInfo.PostInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    requestInfo = RequestInfo(
      description = "Cool stuff"
    ),
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPutNestedLocation = MethodInfo.PutInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    requestInfo = RequestInfo(
      description = "Cool stuff"
    ),
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testDeleteNestedLocation = MethodInfo.DeleteInfo<SimpleLoc.NestedLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
}
