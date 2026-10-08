pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Watcher"
include(":app")
include(":utils")
include(":core:navigation")
include(":core:ui")
include(":core:database")

include(":feature:start:api")
include(":feature:start:impl")

include(":feature:home:api")
include(":feature:home:impl")

include(":feature:calendar:api")
include(":feature:calendar:impl")

include(":feature:rendernode:api")
include(":feature:rendernode:impl")

include(":feature:layoutsandbox:api")
include(":feature:layoutsandbox:impl")

include(":feature:subcomposelayoutsandbox:api")
include(":feature:subcomposelayoutsandbox:impl")

include(":feature:minesweeper:api")
include(":feature:minesweeper:impl")

include(":feature:minesweeper-gamefield:api")
include(":feature:minesweeper-gamefield:impl")

include(":feature:minesweeper-settings:api")
include(":feature:minesweeper-settings:impl")

include(":feature:testgetuserdata")

include(":feature:masterpro:api")
include(":feature:masterpro:impl")
