pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/hammerheadnav/karoo-ext")
            credentials {
                username = providers.gradleProperty("gpruser").getOrElse(System.getenv("GITHUB_USERNAME"))
                password = providers.gradleProperty("gprkey").getOrElse(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}

rootProject.name = "ktor-client-karoo"
include(":app")
include(":lib")
