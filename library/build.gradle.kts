import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val ktorVersion = "1.6.2"
val kotlinVersion = "1.6.0"

plugins {
  kotlin("multiplatform") version "1.6.0"
  kotlin("plugin.serialization") version "1.6.0"
  application
}

group = "io.viamo"
version = "1.0"

repositories {
  mavenCentral()
  jcenter()
}

kotlin {
  kotlinJvm()
  kotlinJs()
  /*kotlinNative()*/

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinVersion-RC")
        implementation("com.benasher44:uuid:0.3.1")
        // TODO: use this for JS/JVM multiplatform once cashapp/square team releases next update: implementation("app.cash.zipline:zipline:0.1.0")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))

        //implementation("io.ktor:ktor-server-tests:1.5.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0-RC")

       // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinVersion-RC")
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation("io.ktor:ktor-server-netty:1.5.2")
        implementation("io.ktor:ktor-html-builder:1.5.2")
        implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
      }
    }
    val jvmTest by getting

    val jsMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.251-kotlin-1.5.31")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.251-kotlin-1.5.31")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.1-pre.251-kotlin-1.5.31")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:5.2.0-pre.251-kotlin-1.5.31")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.1-pre.251-kotlin-1.5.31")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.4-pre.251-kotlin-1.5.31")
      }
    }
    val jsTest by getting

    /*val nativeMain by creating {
      dependsOn(commonMain)
    }

    val nativeTest by creating {
      dependsOn(commonTest)
    }

    // Linux
    val linuxX64Main by getting {
      dependsOn(nativeMain)
    }

    val linuxX64Test by getting {
      dependsOn(nativeTest)
    }

    // Windows
    val mingwX64Main by getting {
      dependsOn(nativeMain)
    }

    val mingwX64Test by getting {
      dependsOn(nativeTest)
    }

    // Mac
    val macosX64Main by getting {
      dependsOn(nativeMain)
    }

    val macosX64Test by getting {
      dependsOn(nativeTest)
    }*/
  }
}

fun isWindows(hostOs: String) = hostOs.startsWith("Windows")

fun KotlinMultiplatformExtension.kotlinJvm() {
  jvm("jvm") {

  compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      useJUnit()
    }
    withJava()
  }
}

fun KotlinMultiplatformExtension.kotlinNative() {

  linuxX64 {
    binaries {
      executable {
        entryPoint = "main"
      }
      sharedLib()
      staticLib()
    }
  }

  mingwX64 {
    binaries {
      executable {
        entryPoint = "main"
      }
    }
  }

  macosX64 {
    binaries {
      executable {
        entryPoint = "main"
      }
    }
  }
}

fun KotlinMultiplatformExtension.kotlinJs() {
  js(IR) {
    binaries.executable()
    /*browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }*/
    nodejs {
    }
  }
}


application {
  mainClass.set("io.viamo.flow.runner.ServerKt")
}

/*tasks.named<Copy>("jvmProcessResources") {
  val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
  from(jsBrowserDistribution)
}*/

tasks.named<JavaExec>("run") {
  dependsOn(tasks.named<Jar>("jvmJar"))
  classpath(tasks.named<Jar>("jvmJar"))
}
