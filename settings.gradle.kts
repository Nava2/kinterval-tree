rootProject.name = "kinterval-tree"

plugins {
  id("com.gradle.enterprise") version "3.14.1"
}

gradleEnterprise {
  buildScan {
    publishAlways()

    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}