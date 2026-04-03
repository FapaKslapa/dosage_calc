// Punto di ingresso del progetto Gradle: definisce il nome del progetto
// e dove Gradle deve cercare i plugin e le dipendenze.
pluginManagement {
    repositories {
        google {
            content {
                // Ottimizzazione: scarica da google.com solo i gruppi che appartengono a Google.
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
    // FAIL_ON_PROJECT_REPOS forza tutti i moduli a usare i repository dichiarati qui,
    // evitando repository "nascosti" nei singoli build.gradle dei moduli.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DosageCalc"
include(":app")
