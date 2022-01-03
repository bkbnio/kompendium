package oas.server

data class ServerVariable(
  val `enum`: Set<String>, // todo enforce not empty
  val default: String,
  val description: String?
)
