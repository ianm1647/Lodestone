import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("eclipse")
    id("idea")
    id("maven-publish")
    id("net.neoforged.moddev").version("0.1.99")
    //id("org.parchmentmc.librarian.forgegradle").version("1.+")
    //id("org.spongepowered.mixin")
}

version = "${property("minecraft_version")}-${property("mod_version")}"
if (System.getenv("BUILD_NUMBER") != null) {
    version = "${property("loader_version")}-${property("mod_version")}.${System.getenv("BUILD_NUMBER")}"
}
group = "${property("mod_group_id")}"

val baseArchivesName = "${property("mod_id")}"
base {
    archivesName.set(baseArchivesName)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("${property("mod_java_version")}"))
    }
}

neoForge {
    // Specify the version of NeoForge to use.
    version = "${property("neo_version")}"

    parchment {
        mappingsVersion = "${property("parchment_mappings_version")}"
        minecraftVersion = "${property("parchment_minecraft_version")}"
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers.add('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        create("client") {
            client()

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", "${property("mod_id")}")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "${property("mod_id")}")
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "${property("mod_id")}")
        }

        create("data") {
            data()

            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file('run-data')

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", "${property("mod_id")}", "--all", "--output", file("src/generated/resources/").getAbsolutePath(), "--existing", file("src/main/resources/").getAbsolutePath())
        }

        // applies to all the run configs above
        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // mostly optional in a single mod project
        // but multi mod projects should define one per mod
        create("${property("mod_id")}") {
            //sourceSet(sourceSets.main)
            //modSource(property("mod_id").sourceSets["main"])
        }
    }
}

// Include resources generated by data generators.
sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Curios maven"
        url = uri("https://maven.theillusivec4.top/")
        content {
            includeGroupByRegex("top\\.theillusivec4.*")
        }
    }
    maven {
        name = "JEI maven"
        url = uri("https://dvs1.progwml6.com/files/maven")
    }
    maven {
        name = "tterrag maven"
        url = uri("https://maven.tterrag.com/")
    }
    maven {
        name = "BlameJared maven"
        url = uri("https://maven.blamejared.com/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    //minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    //if (System.getProperty("idea.sync.active") != "true") {
    //    annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
    //}

    // JEI Dependency
//    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge-api:${jeiVersion}"))
//    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}"))
    //runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge:${jeiVersion}"))
    runtimeOnly("mezz.jei:jei-${property("minecraft_version")}-neoforge:${property("jei_version")}")

    // Curios dependency
    compileOnly("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}:api")
    runtimeOnly("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}")

//    implementation(fg.deobf("com.sammy.malum:malum:${minecraftVersion}-1.6.72"))
}

tasks.withType<ProcessResources> {
    inputs.property("version", version)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(
                mapOf(
                        "neo_version_range" to "${property("neo_version_range")}",
                        "loader_version_range" to "${property("loader_version_range")}",
                        "minecraft_version" to "${property("minecraft_version")}",
                        "minecraft_version_range" to "${property("minecraft_version_range")}",
                        "mod_authors" to "${property("mod_authors")}",
                        "mod_description" to "${property("mod_description")}",
                        "mod_id" to "${property("mod_id")}",
                        "mod_java_version" to "${property("mod_java_version")}",
                        "mod_name" to "${property("mod_name")}",
                        "mod_version" to "${property("version")}",
                        "mod_license" to "${property("mod_license")}"
                )
        )
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}
/*
tasks.withType<Jar> {
    val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
    manifest {
        attributes(mapOf(
                "Specification-Title" to "${property("mod_name")}",
                "Specification-Vendor" to "${property("mod_authors")}",
                "Specification-Version" to '1',
                "Implementation-Title" to "${property("mod_name")}",
                "Implementation-Version" to "${property("version")}",
                "Implementation-Vendor" to "${property("mod_authors")}",
                "Implementation-Timestamp" to now,
        ))
    }
    finalizedBy("reobfJar")
}

 */

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = baseArchivesName
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${System.getenv("local_maven")}")
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}