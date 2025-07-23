plugins {
    id("java")
    id("fabric-loom") version("1.9-SNAPSHOT")
    kotlin("jvm") version ("2.1.0")
    kotlin("plugin.serialization") version "1.9.0" 
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.cobblemon.com/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Fabric API (solo una vez)
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    // Fabric Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // Cobblemon
    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")

    // Counter
    modImplementation("maven.modrinth:cobblemon-counter:${property("counter_version")}")

    // Jedis for Redis
    implementation("redis.clients:jedis:5.1.0")
    include("redis.clients:jedis:5.1.0")

    // Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }

    compileJava {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of("21"))
            }
        }
    }
}