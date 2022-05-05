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

val jnaVersion = "5.9.0"
val gdxVersion = "1.10.0"

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.gradle.maven-publish")
	
	dependencies {
		implementation(kotlin("stdlib"))
		implementation(kotlin("script-runtime"))

		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
		
		implementation("com.csgopoison", "api", "0.1.0")
		implementation("com.csgopoison", "app", "0.1.0")

		implementation("org.gamepoison.internal", "api-native-jna", "0.1.0")
		implementation("org.gamepoison.internal", "api-native-jna-windows", "0.1.0")

		implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")

		implementation("it.unimi.dsi:fastutil:8.5.6")
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