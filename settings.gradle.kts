pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Alif Tech Task"
include(":app")
include(":feature:posts")
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:model")
include(":core:database")
include(":core:network")
