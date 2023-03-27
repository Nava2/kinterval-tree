import Versions.configureJavaToolchain
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.net.URI

plugins {
  kotlin("jvm") version Versions.KOTLIN
  id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
  id("com.jaredsburrows.license") version "0.9.0"
  id("com.autonomousapps.dependency-analysis") version "1.19.0"
  `maven-publish`
  signing
}

buildscript {
  repositories {
    mavenCentral()
  }
}

java {
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

if (System.getenv("CI") == "true") {
  when (val eventName = System.getenv("GITHUB_EVENT_NAME")) {
    "release" -> {
      version = version.toString().substringBefore("-SNAPSHOT")
      logger.info("Deploying version: $version")
    }
    "push", "pull_request" -> Unit
    else -> {
      logger.warn("unknown event: $eventName")
    }
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:31.1-jre")

  testImplementation("org.assertj:assertj-core:3.24.2")

  val junitVersion = "5.9.1"
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

val javadocJar by tasks.register<Jar>("javadocJar") {
  archiveClassifier.set("javadoc")
  from(tasks.named("javadoc"))
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

artifacts {
  archives(sourcesJar)
  archives(javadocJar)
}

signing {
  val signingKeyId: String? by project
  val signingKey: String? by project
  val signingPassword: String? by project
  useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
  sign(configurations.archives.get())
}

publishing {
  publications {
    repositories {
      maven {
        repositories {
          maven {
            name = "ossrh"

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"

            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
              username = System.getenv("OSSRH_USERNAME")
              password = System.getenv("OSSRH_PASSWORD")
            }
          }
        }
      }
    }

    create<MavenPublication>("maven") {
      from(components["kotlin"])
      pom {
        signing {
          sign(publishing.publications["maven"])
          sign(configurations.archives.get())
        }

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
            name.set("Kevin Brightwell")
            email.set("kevin.brightwell2+interval-tree@gmail.com")
          }
        }
        scm {
          url.set("https://github.com/Nava2/kinterval-tree")
        }
      }
    }
  }
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
