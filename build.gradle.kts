plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.homelab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.3.1")
    testImplementation(kotlin("test"))
    testImplementation ("io.kotest:kotest-assertions-core:5.6.2")
    implementation("io.github.serpro69:kotlin-faker:1.14.0")
    // https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-dynamodb
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.12.472")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}