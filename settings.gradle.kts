rootProject.name = "kinterval-tree"

plugins {
  id("com.gradle.enterprise") version "3.17.4"
}

gradleEnterprise {
  buildScan {
    publishAlways()

    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}