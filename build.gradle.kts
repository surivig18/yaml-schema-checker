plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"

}

application {
    mainClass.set("org.schema.YamlSchemaChecker")
}
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("info.picocli:picocli:4.7.7")
    implementation("info.picocli:picocli-shell-jline3:4.7.7")
    implementation("org.yaml:snakeyaml:2.4")
    implementation("org.jline:jline-console-ui:3.30.3")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}