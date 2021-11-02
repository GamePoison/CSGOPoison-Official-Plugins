plugins {
	kotlin("jvm") version "1.5.31"
	`maven-publish`
}

allprojects {
	group = "com.csgopoison.plugins.official"
	
	repositories {
		mavenLocal()
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.gradle.maven-publish")
	
	dependencies {
		implementation(kotlin("stdlib"))
		implementation(kotlin("script-runtime"))
		
		implementation("com.csgopoison", "api", "0.1.0")
	}
	
	java {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
		
		withJavadocJar()
		withSourcesJar()
	}
	
	tasks {
		compileKotlin {
			kotlinOptions.jvmTarget = "11"
		}
		compileTestKotlin {
			kotlinOptions.jvmTarget = "11"
		}
	}
	
	publishing {
		publications {
			create<MavenPublication>("maven") {
				artifactId = project.path.substring(1).replace(':', '-')
				from(components["java"])
			}
		}
	}
}