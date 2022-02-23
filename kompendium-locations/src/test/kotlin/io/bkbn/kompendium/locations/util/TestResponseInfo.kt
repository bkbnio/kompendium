package io.bkbn.kompendium.locations.util

import io.bkbn.kompendium.core.metadata.RequestInfo
import io.bkbn.kompendium.core.metadata.ResponseInfo
import io.bkbn.kompendium.core.metadata.method.DeleteInfo
import io.bkbn.kompendium.core.metadata.method.GetInfo
import io.bkbn.kompendium.core.metadata.method.PostInfo
import io.bkbn.kompendium.core.metadata.method.PutInfo
import io.ktor.http.HttpStatusCode

object TestResponseInfo {
  val testGetSimpleLocation = GetInfo<SimpleLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPostSimpleLocation = PostInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
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
  val testPutSimpleLocation = PutInfo<SimpleLoc, SimpleRequest, SimpleResponse>(
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
  val testDeleteSimpleLocation = DeleteInfo<SimpleLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testGetNestedLocation = GetInfo<SimpleLoc.NestedLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
  val testPostNestedLocation = PostInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
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
  val testPutNestedLocation = PutInfo<SimpleLoc.NestedLoc, SimpleRequest, SimpleResponse>(
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
  val testDeleteNestedLocation = DeleteInfo<SimpleLoc.NestedLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )

  val testGetNestedLocationFromNonLocationClass = GetInfo<NonLocationObject.SimpleLoc.NestedLoc, SimpleResponse>(
    summary = "Location Test",
    description = "A cool test",
    responseInfo = ResponseInfo(
      status = HttpStatusCode.OK,
      description = "A successful endeavor"
    )
  )
}
