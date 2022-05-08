1plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    ksp(project(":processor"))

    testImplementation(project(":annotations"))
    testImplementation(kotlin("test"))
    testImplementation(libs.google.truth)
}

sourceSets {
    test {
        java {
            srcDir(file("build/generated/ksp/test/kotlin"))
        }
    }
}