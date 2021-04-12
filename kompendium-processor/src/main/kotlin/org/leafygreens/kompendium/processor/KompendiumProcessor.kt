package org.leafygreens.kompendium.processor

import com.google.auto.service.AutoService
import java.net.URI
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.annotations.KompendiumContact
import org.leafygreens.kompendium.annotations.KompendiumInfo
import org.leafygreens.kompendium.annotations.KompendiumLicense
import org.leafygreens.kompendium.annotations.KompendiumModule
import org.leafygreens.kompendium.annotations.KompendiumServers
import org.leafygreens.kompendium.models.OpenApiSpecInfoContact
import org.leafygreens.kompendium.models.OpenApiSpecInfoLicense
import org.leafygreens.kompendium.models.OpenApiSpecServer

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class KompendiumProcessor : AbstractProcessor() {

  private val kompendium = Kompendium()

  companion object {
    const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
  }

  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return mutableSetOf(
      KompendiumInfo::class.java.canonicalName,
      KompendiumContact::class.java.canonicalName,
      KompendiumLicense::class.java.canonicalName,
      KompendiumServers::class.java.canonicalName,
      KompendiumModule::class.java.canonicalName
    )
  }

  // TODO Throw error if more than 1 info, contact, etc.?
  override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
    roundEnv?.getElementsAnnotatedWith(KompendiumInfo::class.java)?.forEach {
      val info = it.getAnnotation(KompendiumInfo::class.java)
      processKompendiumInfo(info)
    }
    roundEnv?.getElementsAnnotatedWith(KompendiumContact::class.java)?.forEach {
      val contact = it.getAnnotation(KompendiumContact::class.java)
      processKompendiumContact(contact)
    }
    roundEnv?.getElementsAnnotatedWith(KompendiumLicense::class.java)?.forEach {
      val license = it.getAnnotation(KompendiumLicense::class.java)
      processKompendiumLicense(license)
    }
    roundEnv?.getElementsAnnotatedWith(KompendiumServers::class.java)?.forEach {
      val servers = it.getAnnotation(KompendiumServers::class.java)
      processKompendiumServers(servers)
    }
    return true
  }

  private fun processKompendiumInfo(info: KompendiumInfo) {
    kompendium.spec.info?.apply {
      this.title = info.title
      this.version = info.version
      info.description.blankToNull()?.let { desc ->
        this.description = desc
      }
      info.termsOfService.blankToNull()?.let { tos ->
        this.termsOfService = URI(tos)
      }
    }
  }

  private fun processKompendiumContact(contact: KompendiumContact) {
    kompendium.spec.info?.apply {
      this.contact = OpenApiSpecInfoContact(
        name = contact.name
      ).apply {
        contact.url.blankToNull()?.let { url ->
          this.url = URI(url)
        }
        contact.email.blankToNull()?.let { email ->
          this.email = email
        }
      }
    }
  }

  private fun processKompendiumLicense(license: KompendiumLicense) {
    kompendium.spec.info?.apply {
      this.license = OpenApiSpecInfoLicense(
        name = license.name
      ).apply {
        license.url.blankToNull()?.let { url ->
          this.url = URI(url)
        }
      }
    }
  }

  private fun processKompendiumServers(servers: KompendiumServers) {
    servers.urls.forEach { url ->
      kompendium.spec.servers?.add(OpenApiSpecServer(URI(url)))
    }
  }

  private fun String.blankToNull(): String? = ifBlank {
    null
  }

}
