plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
    id("java")
    id("com.google.protobuf") version "0.8.18" apply false
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    idea
}

sourceSets {
    java {
    }
    kotlin {
        sourceSets.getByName("main").resources.srcDirs("stub/build/generated/source/proto/main/kotlin")
    }
}


allprojects {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

ext["grpcVersion"] = "1.39.0" // need to wait for grpc kotlin to move past this
ext["grpcKotlinVersion"] = "1.2.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.19.1"
ext["coroutinesVersion"] = "1.5.2"

description = "Robocode - Build the best - destroy the rest!"

val ossrhUsername: String by project
val ossrhPassword: String by project

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))//staging/deploy/maven2/
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
            stagingProfileId.set("c7f511545ccf8")
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}
