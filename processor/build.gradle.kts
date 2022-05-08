plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    api(project(":annotations"))

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
}