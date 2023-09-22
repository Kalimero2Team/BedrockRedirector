plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.kalimero2.team"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-snapshots")
    maven("https://repo.opencollab.dev/maven-releases")
}

dependencies {
    implementation("org.cloudburstmc.protocol:bedrock-codec:3.0.0.Beta1-20230908.171156-106")
    implementation("org.cloudburstmc.protocol:bedrock-connection:3.0.0.Beta1-20230908.171156-105")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.7.1")
    implementation("org.slf4j:slf4j-simple:1.7.21")
}

application {
    mainClass.set("com.kalimero2.team.bedrockredirector.Server")
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir.resolve("run")
    workingDir.mkdir()
}
