import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("io.gitlab.arturbosch.detekt").version("1.15.0")
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

group = "sea-4-key-metrics-service"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    jcenter {
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.0")
    }
}

sourceSets {
    create("unitTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        java.srcDir("src/test/kotlin")
        resources.srcDir("src/test/kotlin")
        resources.exclude("**/*.kt")
    }

    create("apiTest") { 
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output 
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        java.srcDir("src/api-test/kotlin")
        resources.srcDir("src/api-test/kotlin")
        resources.exclude("**/*.kt")
    }
}

val apiTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["apiTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["apiTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

    configurations.compile{
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }

    testImplementation("org.springframework.boot:spring-boot-starter-test")  {
        exclude("org.junit.vintage","junit-vintage-engine")
    }
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testImplementation("com.intuit.karate:karate-junit5:0.9.6")
    testImplementation("com.intuit.karate:karate-apache:0.9.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")

    configurations.testCompile{
        exclude("ch.qos.logback", "logback-classic")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("karate.options", System.getProperty("karate.options"))
    systemProperty("karate.env", System.getProperty("karate.env"))
    outputs.upToDateWhen { false }
}

val apiTest = task<Test>("apiTest") {
    description = "Runs api tests."
    group = "verification"

    testClassesDirs = sourceSets["apiTest"].output.classesDirs
    classpath = sourceSets["apiTest"].runtimeClasspath
}


detekt {
    toolVersion = "1.15.0"
    config = files("gradle/detekt/detekt.yml")
    buildUponDefaultConfig = true

    reports {
        xml.enabled = false
        txt.enabled = false
        html.enabled = true
        html.destination = file("${buildDir}/reports/detekt/detekt.html")
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    exclude("fourkeymetrics/Application.kt")
}

apply(from = "gradle/git-hooks/install-git-hooks.gradle")
apply(from = "gradle/jacoco.gradle")