plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

group = "com.dpdemo"
version = "0.0.1-SNAPSHOT"
val projectVersion = "0.0.1-SNAPSHOT"

dependencies {
	//spring
	implementation(libs.springBootAdmin)
	implementation(libs.springBootWeb)
	implementation(libs.asyncHealthIndicator)

	//Kafka
	implementation(libs.springKafka)
	implementation(libs.kafkaStreams)
	implementation(libs.kafkaStreamsAvroSerde)
	implementation(libs.kafkaStreamsJsonSerde)

	//other libs
	implementation(libs.logstashLogbackEncoder)
	implementation(libs.micrometerPrometheus)

	//processors
	compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)

	//tests
	testImplementation(libs.springBootSarterTest)
	testImplementation(libs.kafkaStreamsTestUtils)
	testCompileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)

}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	val confluentUrl = "https://packages.confluent.io/maven/"
	val jFrogUrl = "https://artifactory.ext.prod.common.betlabs.io:443/artifactory/common-gradle-local"

	mavenCentral()
	gradlePluginPortal()
	maven {
		name = "jFrog"
		url = uri(jFrogUrl)
		authentication {
			create<BasicAuthentication>("basic")
		}
		credentials(PasswordCredentials::class)
	}
	mavenLocal()
	maven(confluentUrl)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Copy> {
	from("src/main/resources") {
		include("application.yaml")
		duplicatesStrategy = DuplicatesStrategy.INCLUDE // or DuplicatesStrategy.EXCLUDE or DuplicatesStrategy.WARN
		filter { fileContent ->
			fileContent.replace("\${projectVersion}", projectVersion)
		}
	}
}
