// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
