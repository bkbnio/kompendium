package org.leafygreens.kompendium.util

import java.io.File
import java.net.URI
import org.leafygreens.kompendium.models.oas.OpenApiSpec
import org.leafygreens.kompendium.models.oas.OpenApiSpecComponents
import org.leafygreens.kompendium.models.oas.OpenApiSpecExternalDocumentation
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfo
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.oas.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.oas.OpenApiSpecMediaType
import org.leafygreens.kompendium.models.oas.OpenApiSpecOAuthFlow
import org.leafygreens.kompendium.models.oas.OpenApiSpecOAuthFlows
import org.leafygreens.kompendium.models.oas.OpenApiSpecParameter
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItem
import org.leafygreens.kompendium.models.oas.OpenApiSpecPathItemOperation
import org.leafygreens.kompendium.models.oas.OpenApiSpecReferenceObject
import org.leafygreens.kompendium.models.oas.OpenApiSpecRequest
import org.leafygreens.kompendium.models.oas.OpenApiSpecResponse
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaArray
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaRef
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaSecurity
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaString
import org.leafygreens.kompendium.models.oas.OpenApiSpecServer
import org.leafygreens.kompendium.models.oas.OpenApiSpecTag

object TestHelpers {
  fun getFileSnapshot(fileName: String): String {
    val snapshotPath = "src/test/resources"
    val file = File("$snapshotPath/$fileName")
    return file.readText()
  }
}
