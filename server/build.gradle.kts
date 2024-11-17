plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.laev"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test") // Spring Boot starter test for core test utilities and JUnit 5
	testImplementation("io.mockk:mockk:1.13.4") // MockK for Kotlin-friendly mocking
	testImplementation("com.ninja-squad:springmockk:4.0.2") // SpringMockK for integration with Spring's @MockBean
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	systemProperty("spring.profiles.active", "test") // Activates the "test" profile during test execution
}
