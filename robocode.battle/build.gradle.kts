plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.host"))
    implementation("org.picocontainer:picocontainer:2.14.2")
    runtimeOnly(project(":robocode.repository"))
    implementation(project(":robocode.grpc"))
}

description = "Robocode Battle"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/battle/Module.java")
    }
    jar {
        dependsOn("javadoc")
    }
}