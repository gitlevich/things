import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val axonVersion: String by project
val inspectorAxonVersion: String by project
val jupiterVersion: String by project
val assertjVersion: String by project
val mockkVersion: String by project
val reactorCoreVersion: String by project
val kotlinVersion: String by project
val reactorKotlinExt: String by project

plugins {
    val kotlinGradlePluginVersion = "1.9.10"
    val springBootVersion = "3.1.5"

    base
    java
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version kotlinGradlePluginVersion
    kotlin("plugin.noarg") version kotlinGradlePluginVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.things"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.axonframework:axon-spring-boot-starter")
    implementation("org.axonframework.extensions.kotlin:axon-kotlin")
    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinExt")
    implementation("io.projectreactor.addons:reactor-extra")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.axonframework:axon-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${extra["axonVersion"]}")
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.1.5") {
            bomProperty("kotlin.version", "1.9.10")
        }
    }
}

val kotlinTest: Configuration by configurations.creating

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.test {
    useJUnitPlatform()
}
