package io.bkbn.kompendium.core.util

import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.OptionsInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
import io.bkbn.kompendium.oas.payload.Parameter

interface SpecConfig {
  var tags: Set<String>
  var parameters: List<Parameter>
  var get: GetInfo?
  var post: PostInfo?
  var put: PutInfo?
  var delete: DeleteInfo?
  var patch: PatchInfo?
  var head: HeadInfo?
  var options: OptionsInfo?
  var security: Map<String, List<String>>?
}
