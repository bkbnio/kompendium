package org.leafygreens.kompendium.util

// Take from https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
object KompendiumHttpCodes {

  // Informational responses
  const val CONTINUE = 100
  const val SWITCHING_PROTOCOL = 101
  const val PROCESSING = 102
  const val EARLY_HINTS = 103

  // Successful responses
  const val OK = 200
  const val CREATED = 201
  const val ACCEPTED = 202
  const val NON_AUTHORITATIVE_INFORMATION = 203
  const val NO_CONTENT = 204
  const val RESET_CONTENT = 205
  const val PARTIAL_CONTENT = 206
  const val MULTI_STATUS = 207
  const val ALREADY_REPORTED = 208
  const val IM_USED = 226

  // Redirection messages
  const val MULTIPLE_CHOICE = 300
  const val MOVED_PERMANENTLY = 301
  const val FOUND = 302
  const val SEE_OTHER = 303
  const val NOT_MODIFIED = 304
  @Deprecated("Deprecated due to security concerns regarding in-band configuration of a proxy")
  const val USE_PROXY = 305
  @Deprecated("This response code is no longer used; it is just reserved.")
  const val UNUSED = 306
  const val TEMPORARY_REDIRECT = 307
  const val PERMANENT_REDIRECT = 308

  // Client Response Errors
  const val BAD_REQUEST = 400
  const val UNAUTHORIZED = 401
  const val PAYMENT_REQUIRED = 402
  const val FORBIDDEN = 403
  const val NOT_FOUND = 404
  const val METHOD_NOT_ALLOWED = 405
  const val NOT_ACCEPTABLE = 406
  const val PROXY_AUTHENTICATION_REQUIRED = 407
  const val REQUEST_TIMEOUT = 408
  const val CONFLICT = 409
  const val GONE = 410
  const val LENGTH_REQUIRED = 411
  const val PRECONDITION_FAILED = 412
  const val PAYLOAD_TOO_LARGE = 413
  const val URI_TOO_LONG = 414
  const val UNSUPPORTED_MEDIA_TYPE = 415
  const val RANGE_NOT_SATISFIABLE = 416
  const val EXPECTATION_FAILED = 417
  const val IM_A_TEAPOT = 418
  const val MISDIRECTED_REQUEST = 421
  const val UNPROCESSABLE_ENTITY = 422
  const val LOCKED = 423
  const val FAILED_DEPENDENCY = 424
  const val TOO_EARLY = 425
  const val UPGRADE_REQUIRED = 426
  const val PRECONDITION_REQUIRED = 428
  const val TOO_MANY_REQUESTS = 429
  const val REQUEST_HEADER_FIELDS_TOO_LARGE = 431
  const val UNAVAILABLE_FOR_LEGAL_REASONS = 451

  // Server Error Responses
  const val INTERNAL_SERVER_ERROR = 500
  const val NOT_IMPLEMENTED = 501
  const val BAD_GATEWAY = 502
  const val SERVICE_UNAVAILABLE = 503
  const val GATEWAY_TIMEOUT = 504
  const val HTTP_VERSION_NOT_SUPPORTED = 505
  const val VARIANT_ALSO_NEGOTIATES = 506
  const val INSUFFICIENT_STORAGE = 507
  const val LOOP_DETECTED = 508
  const val NOT_EXTENDED = 510
  const val NETWORK_AUTHENTICATION_REQUIRED = 511

}
