import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `build-scan`
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.dokka") version "0.10.0"
}

group = "com.github.achrafamil"
version = "0.1.1"

val sourceSets: SourceSetContainer by project

//sonatype login
val REPOSITORY_USER_NAME: String by project
val REPOSITORY_PASSWORD: String by project

//Artifact signing PGP key
val SIGNING_KEY_ID: String by project
val SIGNING_PASSWORD: String by project
val SIGNING_SECRET_KEY_RING_FILE: String by project

allprojects {
    extra["signing.keyId"] = SIGNING_KEY_ID
    extra["signing.password"] = SIGNING_PASSWORD
    extra["signing.secretKeyRingFile"] = SIGNING_SECRET_KEY_RING_FILE
}

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
    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        classifier = "sources"
        from(sourceSets["main"].allSource)
    }

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
                pom {
                    name.set("Splendor API")
                    description.set("Kotlin library to play (and experiment) Splendor, the board game.")
                    url.set("https://github.com/achrafamil/splendor-api")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("achrafamil")
                            name.set("Achraf Amil")
                            email.set("achraf.amil@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git@github.com:achrafamil/splendor-api.git")
                        developerConnection.set("scm:git@github.com:achrafamil/splendor-api.git")
                        url.set("https://github.com/achrafamil/splendor-api")
                    }
                }

                from(components["java"])
                artifact(sourcesJar)
                artifact(dokkaJar)
            }

            signing {
                sign(publishing.publications["default"])
            }
        }
        repositories {
            maven {
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                credentials {
                    username = REPOSITORY_USER_NAME
                    password = REPOSITORY_PASSWORD
                }
            }
        }
    }
}
