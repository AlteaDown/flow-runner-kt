package io.viamo.flow.runner

actual class Platform actual constructor() {
  actual val platform: String
    get() = "linuxX64-Native"
}
