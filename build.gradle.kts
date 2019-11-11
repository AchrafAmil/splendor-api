import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `build-scan`
    `maven-publish`
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.dokka") version "0.10.0"
}

group = "com.github.achrafamil"
version = "0.1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", "1.3.41"))

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")

    publishAlways()
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }

    val dokkaJar by tasks.creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        classifier = "javadoc"
        from(dokka)
    }

    publishing {
        publications {
            create<MavenPublication>("default") {
                from(components["java"])
                artifact(dokkaJar)
            }
        }
        repositories {
            maven {
                url = uri("$buildDir/repository")
            }
        }
    }
}
