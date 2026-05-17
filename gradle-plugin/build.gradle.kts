plugins {
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("langManagerPlugin") {
            id = "dev.darkblade.langmanager"
            implementationClass = "dev.darkblade.langmanager.gradle.LangManagerPlugin"
        }
    }
}

dependencies {
    // Gradle API comes from java-gradle-plugin automatically.
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
