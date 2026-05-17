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

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
