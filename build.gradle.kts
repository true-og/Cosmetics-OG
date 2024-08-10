plugins {
    java // Tell gradle this is a java project.
    id("io.github.goooler.shadow") version "8.1.8"
    eclipse // Import eclipse plugin for IDE integration.
    kotlin("jvm") version "1.9.23" // Import kotlin jvm plugin for kotlin/java integration.
}

java {
    // Declare java version.
    sourceCompatibility = JavaVersion.VERSION_17
}

group = "com.mediusecho" // Declare bundle identifier.
version = "4.6.1" // Declare plugin version (will be in .jar).
val apiVersion = "1.19" // Declare minecraft server target version.

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "version" to version,
        "apiVersion" to apiVersion
    )

    inputs.properties(props) // Indicates to rerun if version changes.

    filesMatching("plugin.yml") {
        expand(props)
    }
}

repositories {
    mavenCentral()

    // Purpur repository
    maven {
        url = uri("https://repo.purpurmc.org/snapshots")
    }

    // SuperVanish repository
    maven {
        url = uri("https://mavenrepo.cubekrowd.net/artifactory/repo/")
    }

    // VaultAPI repository
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT") // Purpur API

    // bStats
    implementation("org.bstats:bstats-bukkit:2.2.1")

    // HikariCP
    implementation("com.zaxxer:HikariCP:3.3.1")

    // SLF4J
    implementation("org.slf4j:slf4j-nop:1.7.25")

    // SuperVanish
    compileOnly("de.myzelyam:SuperVanish:6.0.4")

    // TokenManager
    compileOnly("com.github.Realizedd:TokenManager:3.2.4") {
        exclude(group = "*", module = "*")
    }

    // VaultAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }

    // JetBrains Annotations
    compileOnly("org.jetbrains:annotations:16.0.2")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.shadowJar {
    exclude("org.bstats.*") // Exclude the bStats package from being shadowed.
    minimize()
}

tasks.jar {
    dependsOn(tasks.shadowJar)
    archiveClassifier.set("part")
}

tasks.shadowJar {
    archiveClassifier.set("") // Use empty string instead of null
    from("LICENSE") {
        into("/")
    }
}

tasks.jar {
    dependsOn("shadowJar")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.compilerArgs.add("-Xlint:deprecation") // Triggers deprecation warning messages.
    options.encoding = "UTF-8"
    options.isFork = true
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.GRAAL_VM
    }
}
