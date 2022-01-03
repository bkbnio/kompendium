package io.bkbn.kompendium.oas.path

import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.server.Server

data class Path(
  var get: PathOperation? = null,
  var put: PathOperation? = null,
  var post: PathOperation? = null,
  var delete: PathOperation? = null,
  var options: PathOperation? = null,
  var head: PathOperation? = null,
  var patch: PathOperation? = null,
  var trace: PathOperation? = null,
  var servers: List<Server>? = null,
  var parameters: List<Parameter>? = null
)
