import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.jvm.toolchain.JvmVendorSpec

object Versions {
  const val JVM_BYTECODE_TARGET = 17
  const val KOTLIN = "1.8.21"
  const val DETEKT = "1.22.0"

  fun JavaToolchainSpec.configureJavaToolchain() {
    languageVersion.set(JavaLanguageVersion.of(Versions.JVM_BYTECODE_TARGET))
    vendor.set(JvmVendorSpec.ADOPTIUM)
  }
}

