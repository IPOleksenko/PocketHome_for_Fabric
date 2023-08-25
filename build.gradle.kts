val gitVersion: groovy.lang.Closure<String> by extra

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.git)
}

group = "dev.ipoleksenko"
version = gitVersion()

repositories {
}

loom {
	splitEnvironmentSourceSets()

	mods.register("pockethome") {
		sourceSet(sourceSets["main"])
	}
}

val modInclude: Configuration by configurations.creating {
	configurations.modImplementation.get().extendsFrom(this)
	configurations.include.get().extendsFrom(this)
}

dependencies {
	minecraft(libs.minecraft)
	mappings("net.fabricmc:yarn:${libs.versions.yarn.mappings.get()}:v2")

	modImplementation(libs.fabric.loader)
//	modImplementation(libs.fabric.api)

//	listOf().forEach {
//		modInclude(fabricApi.module(it, libs.versions.fabric.api.get()))
//	}

//	shadowImplementation(libs.nightconfig.core)
//	shadowImplementation(libs.nightconfig.toml)

//	modApi(libs.modmenu)
//	modApi(libs.clothconfig) {
//		exclude(group = "net.fabricmc.fabric-api")
//	}
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
