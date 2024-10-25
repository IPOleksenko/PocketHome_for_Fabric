val gitVersion: groovy.lang.Closure<String> by extra

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.git)
}

group = "dev.ipoleksenko"
version = gitVersion()

repositories {
	maven("https://maven.nucleoid.xyz/")
}

dependencies {
	minecraft(libs.minecraft)
	mappings("net.fabricmc:yarn:${libs.versions.yarn.mappings.get()}:v2")
	modImplementation(libs.fabric.loader)
	// Include Fabric API in the mod
	modImplementation(libs.fabric.api)


	modImplementation(libs.fantasy)
	include(libs.fantasy)

	if (project.hasProperty("withFabricAPI")) {
		include(libs.fabric.api)
		setOf("fabric-events-interaction-v0").forEach {
			modImplementation(fabricApi.module(it, libs.versions.fabric.api.get()))
			include(fabricApi.module(it, libs.versions.fabric.api.get()))
		}
	}
}

tasks {
	compileJava {
		options.encoding = Charsets.UTF_8.name()
		options.release.set(17)
	}

	processResources {
		inputs.property("version", project.version)
		filesMatching("fabric.mod.json") {
			expand("version" to project.version)
		}
	}

	jar {
		from(file("LICENSE"))
	}
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}