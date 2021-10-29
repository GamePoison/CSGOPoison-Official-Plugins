plugins {
	kotlin("jvm") version "1.5.31"
}

allprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	
	group = "com.csgopoison.plugins.official"
	
	repositories {
		mavenLocal()
		mavenCentral()
	}
	
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
}