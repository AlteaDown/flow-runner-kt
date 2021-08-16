plugins {
    kotlin("js") version "1.5.10"
}

group = "me.beatd"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs {

        }
    }
}
