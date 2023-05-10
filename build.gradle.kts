plugins {
    java
    application
}

group = "org.utility.lib"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    compileOnly(fileTree("libs"))
}
application {
    mainClass.set("java.main.Main")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks{
    jar {
        archiveBaseName.set("UtilityLibJava")
        archiveVersion.set("0.1.1")
    }
}


