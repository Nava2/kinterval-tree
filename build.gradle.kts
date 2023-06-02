import Versions.configureJavaToolchain
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
  kotlin("jvm") version Versions.KOTLIN
  id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
  id("com.jaredsburrows.license") version "0.9.2"
  id("com.autonomousapps.dependency-analysis") version "1.19.0"
  `maven-publish`
  signing
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

buildscript {
  repositories {
    mavenCentral()
  }
}

java {
  withJavadocJar()
  withSourcesJar()

  toolchain {
    configureJavaToolchain()
  }
}

kotlin {
  jvmToolchain {
    configureJavaToolchain()
  }
}

apply {
  plugin("com.jaredsburrows.license")
  plugin("org.jetbrains.kotlin.jvm")
  plugin("io.gitlab.arturbosch.detekt")
  plugin("maven-publish")
}

group = "net.navatwo"
archivesName.set("kinterval-tree")
version = "0.1.0-SNAPSHOT"

val isRelease = System.getenv("RELEASE") != null

if (isRelease) {
  version = version.toString().substringBefore("-SNAPSHOT")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:31.1-jre")

  testImplementation("org.assertj:assertj-core:3.24.2")

  val junitVersion = "5.9.3"
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name.set("kinterval-tree")
        description.set("Kotlin implementation of an interval-tree")
        url.set("https://github.com/Nava2/kinterval-tree")

        licenses {
          license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
          }
        }
        developers {
          developer {
            name.set("Mason M Lai")
          }
          developer {
            id.set("Nava2")
            name.set("Kevin Brightwell")
            email.set("kevin.brightwell2+interval-tree@gmail.com")
          }
        }
        scm {
          url.set("https://github.com/Nava2/kinterval-tree.git")
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

      username.set(System.getenv("OSSRH_USERNAME"))
      password.set(System.getenv("OSSRH_PASSWORD"))
    }
  }
}

signing {
  fun findProperty(name: String): String? {
    val propertyName = "ORG_GRADLE_PROJECT_$name"
    return project.findProperty(propertyName) as? String
      ?: System.getenv("ORG_GRADLE_PROJECT_$name")
  }

  val signingKeyId: String? = findProperty("signingKeyId")
  val signingKey: String? = findProperty("signingKey")
  val signingPassword: String? = findProperty("signingPassword")

  useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)

  sign(publishing.publications["maven"])
}

licenseReport {
  generateTextReport = true
  generateHtmlReport = true
  generateCsvReport = false
  generateJsonReport = false
}

tasks.test {
  useJUnitPlatform()
}

detekt {
  parallel = true
  autoCorrect = true

  buildUponDefaultConfig = true // preconfigure defaults
  config = files("$rootDir/config/detekt-config.yml")

  allRules = false // activate all available (even unstable) rules.
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
}

tasks.named("check") {
  dependsOn(tasks.named("projectHealth"))
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = "18"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
  jvmTarget = "18"
}

tasks.withType<Detekt>().configureEach {
  reports {
    html.required.set(true)
  }
}
