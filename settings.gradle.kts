rootProject.name = "kinterval-tree"

plugins {
  id("com.gradle.enterprise") version "3.16.1"
}

gradleEnterprise {
  buildScan {
    publishAlways()

    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}