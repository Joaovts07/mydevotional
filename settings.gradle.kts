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
        maven("https://jitpack.io") {
            content { includeGroup("com.github.Joaovts07.Mylogin") }
        }
    }
}

rootProject.name = "MyDevotional"

// Build loginlib from a sibling Mylogin checkout instead of JitPack: ./gradlew -PlocalLoginlib ...
if (providers.gradleProperty("localLoginlib").isPresent) {
    includeBuild("../Mylogin") {
        dependencySubstitution {
            substitute(module("com.github.Joaovts07.Mylogin:loginlib")).using(project(":loginlib"))
        }
    }
}
include(":app")
