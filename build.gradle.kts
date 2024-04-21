plugins {
    kotlin("jvm") version "1.9.0"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("org.example.MainApp") // Укажи здесь полное имя главного класса Kotlin
}