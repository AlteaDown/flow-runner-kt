package io.viamo.flow.runner.flowspec

import kotlinx.serialization.Serializable

/**
 * Language descriptor used within Flows and Resources: https://floip.gitbook.io/flow-specification/flows#language-objects-and-identifiers
 */
interface ILanguage {
  /**
   * Language Identifier, e.g. "eng-female", described in https://floip.gitbook.io/flow-specification/flows#language-identifiers
   */
  val id: String

  /**
   * Human-readable description for this language and variant.
   */
  val label: String?

  /**
   * ISO 639-3 code for the language. This is a 3-letter String, e.g. "eng".
   * "mis" is the ISO 639-3 code for languages not yet included in ISO 639-3.
   *
   * @pattern ^[a-z][a-z][a-z]$
   */
  val iso_639_3: String

  /**
   * Where multiple languages/content sets are used with the same ISO 639-3 code, variant describes the specialization, e.g. "east_africa".
   */
  val variant: String?

  /**
   * The BCP 47 locale code for this language, e.g. "en-GB".
   * These codes are often useful in conjunction with speech synthesis and speech recognition tools.
   */
  val bcp_47: String?
}

@Serializable
data class Language(
  override val id: String,
  override val label: String?,
  override val iso_639_3: String,
  override val variant: String?,
  override val bcp_47: String?
) : ILanguage