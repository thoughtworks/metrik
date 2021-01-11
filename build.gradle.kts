import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	idea
	jacoco
	application
	id("org.springframework.boot") version "2.4.1"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
}

group = "sea-4-key-metrics-service"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
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
		xml.isEnabled = false
		csv.isEnabled = false
		html.isEnabled = true
		html.destination = file("${buildDir}/reports/coverage")
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.9".toBigDecimal()
			}
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
