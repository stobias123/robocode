import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":robocode.api"))
    implementation("org.takes:takes:1.19")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    implementation(kotlin("stdlib-jdk8"))
}

description = "Robocode Samples"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    register("copyContent", Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        from("src/main/java") {
            include("**")
        }
        into("../.sandbox/robots")
    }
    register("copyClasses", Copy::class) {
        dependsOn(configurations.runtimeClasspath)

        from(compileJava)
        into("../.sandbox/robots")
    }
    register("copyKotlin", Copy::class) {
        dependsOn(configurations.runtimeClasspath)

        from(compileKotlin)
        into("../.sandbox/robots")
    }
    javadoc {
        source = sourceSets["main"].java
        include("**/*.java")
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        dependsOn("copyContent")
        dependsOn("copyClasses")
        dependsOn("copyKotlin")
        dependsOn("javadoc")
        from("src/main/java") {
            include("**")
        }
        from("src/main/kotlin") {
            include("**")
        }
        from("src/main/resources") {
            include("**")
        }
    }
}
repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}