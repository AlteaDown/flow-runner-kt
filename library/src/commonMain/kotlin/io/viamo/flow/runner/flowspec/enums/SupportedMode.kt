package io.viamo.flow.runner.flowspec.enums

import kotlinx.serialization.Serializable

/**
 * Supported modes for Flows and Resources: https://floip.gitbook.io/flow-specification/flows#modes
 */
@Serializable
enum class SupportedMode {
  TEXT,
  SMS,
  USSD,
  IVR,
  RICH_MESSAGING,
  OFFLINE,
}
