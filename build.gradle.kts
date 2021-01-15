import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    jacoco
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

application {
    mainClass.set("fourkeymetrics.Application")
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        csv.isEnabled = false
        xml.isEnabled = true
        html.isEnabled = true
    }

    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("fourkeymetrics/Application.class")
            }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.95".toBigDecimal()
            }
        }

        rule {
            element = "METHOD"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }

        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
            /**
             * filter class which not need to test
             * excludes = listOf("fourkeymetrics.service.HelloService")
             */
            excludes = listOf("fourkeymetrics.service.HelloService")
        }

        rule {
            element = "METHOD"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
            /**
             * filter method which not need to test
             * excludes = listOf("fourkeymetrics.service.HelloService.sayHello(java.lang.String)")
             */
            excludes = listOf("fourkeymetrics.service.HelloService.sayHello(java.lang.String)")
        }
    }

    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("fourkeymetrics/Application.class")
            }
    )
}

tasks.check {
    dependsOn(":jacocoTestReport", ":jacocoTestCoverageVerification")
}

detekt {
    toolVersion = "1.15.0"
    config = files("config/detekt/detekt.yml")
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

apply(from = "config/git-hooks/install-git-hooks.gradle")