plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation("org.takes:takes:1.19")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
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
    javadoc {
        source = sourceSets["main"].java
        include("**/*.java")
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        dependsOn("copyContent")
        dependsOn("copyClasses")
        dependsOn("javadoc")
        from("src/main/java") {
            include("**")
        }
        from("src/main/resources") {
            include("**")
        }
    }
}