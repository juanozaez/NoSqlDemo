plugins {
    kotlin("jvm") version "2.4.0-Beta2"
    application
}

group = "com.homelab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.3.1")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.12.472")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.9")
    implementation("org.mongodb:mongodb-driver-sync:4.10.1")
    implementation("com.mysql:mysql-connector-j:8.3.0")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.github.serpro69:kotlin-faker:1.14.0")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}