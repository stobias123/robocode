plugins {
    `java-library`
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}

kotlin {

}

sourceSets {
    main.kotlin.srcDirs += 'src/main/kotlin'
    main.kotlin.srcDirs += 'stub/build/generated/source/proto/main/kotlin'
    main.java.srcDirs += 'stub/build/generated/source/proto/main/java'
}
