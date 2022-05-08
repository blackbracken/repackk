plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    ksp(project(":processor"))

    testImplementation(project(":annotations"))
    testImplementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.1.3")
}

sourceSets {
    test {
        java {
            srcDir(file("build/generated/ksp/test/kotlin"))
        }
    }
}