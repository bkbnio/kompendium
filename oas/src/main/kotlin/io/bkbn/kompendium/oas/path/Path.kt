package io.bkbn.kompendium.oas.path

import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.server.Server
import kotlinx.serialization.Serializable

/**
 * Describes the operations available on a single path.
 *
 * https://spec.openapis.org/oas/v3.1.0#path-item-object
 *
 * @param summary An optional, string summary, intended to apply to all operations in this path.
 * @param description An optional, string description, intended to apply to all operations in this path.
 * @param get A definition of a GET operation on this path.
 * @param put A definition of a GET operation on this path.
 * @param post A definition of a GET operation on this path.
 * @param delete A definition of a GET operation on this path.
 * @param options A definition of a GET operation on this path.
 * @param head A definition of a GET operation on this path.
 * @param patch A definition of a GET operation on this path.
 * @param trace A definition of a GET operation on this path.
 * @param servers An alternative server array to service all operations in this path.
 * @param parameters A list of parameters that are applicable for all the operations described under this path.
 * These parameters can be overridden at the operation level,
 * but cannot be removed there. The list MUST NOT include duplicated parameters.
 */
@Serializable
data class Path(
  var summary: String? = null,
  var description: String? = null,
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
