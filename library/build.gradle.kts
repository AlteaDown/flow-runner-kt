plugins {
  kotlin("multiplatform") version "1.5.10"
  application
}

group = "me.beatd"
version = "1.0-SNAPSHOT"

repositories {
  jcenter()
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      useJUnit()
    }
    withJava()
  }
  js(IR) {
    binaries.executable()
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
  }
  val hostOs = System.getProperty("os.name")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64(hostOs) -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  nativeTarget.apply {
    binaries {
      executable {
        entryPoint = "main"
      }
    }
  }
  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
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
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.206-kotlin-1.5.10")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.206-kotlin-1.5.10")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.0-pre.206-kotlin-1.5.10")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:5.2.0-pre.206-kotlin-1.5.10")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.0.5-pre.206-kotlin-1.5.10")
        implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.3-pre.206-kotlin-1.5.10")
      }
    }
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
  }
}

application {
  mainClass.set("ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
  val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
  from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
  dependsOn(tasks.named<Jar>("jvmJar"))
  classpath(tasks.named<Jar>("jvmJar"))
}

fun isMingwX64(hostOs: String) = hostOs.startsWith("Windows")
