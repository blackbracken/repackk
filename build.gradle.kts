plugins {
    kotlin("jvm")
}

group = "black.bracken.repackk"
version = "0.1.0-SNAPSHOT"

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.21"))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}