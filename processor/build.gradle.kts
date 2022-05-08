plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    api(project(":annotations"))

    implementation(libs.kotlin.reflect)
    implementation(libs.google.ksp)
}