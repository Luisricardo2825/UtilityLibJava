plugins {
    java
    application
}

group = "org.example"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree("libs"))
}
application {
    mainClass.set("java.main.Main")
}



