
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}


val moduleName = "ktor-client-karoo"
val libVersion = "1.0.0"

android {
    namespace = "de.jonasfranz.ktor.client.karoo"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        buildConfigField("String", "LIB_VERSION", "\"$libVersion\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        aidl = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.ktor.client.core)
    implementation(libs.karoo.ext)
}


// To build an publish locally: gradle lib:assemblerelease lib:publishtomavenlocal
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jonasfranz/ktor-client-karoo")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("karoo-ext") {
            artifactId = moduleName
            groupId = "de.jonasfranz"
            version = libVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
