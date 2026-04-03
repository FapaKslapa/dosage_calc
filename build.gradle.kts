// Build script a livello di progetto (root).
// NON aggiungere dipendenze dell'app qui. Questo file serve solo a
// dichiarare i plugin che verranno APPLICATI (non solo disponibili)
// nei moduli figli.
plugins {
    // Dichiara i plugin ma non li applica ancora (apply false).
    // Saranno i singoli moduli (es. :app) ad applicarli esplicitamente.
    alias(libs.plugins.android.application)    apply false
    alias(libs.plugins.kotlin.android)         apply false
    alias(libs.plugins.kotlin.compose)         apply false
    alias(libs.plugins.kotlin.serialization)   apply false
    alias(libs.plugins.hilt.android)           apply false
    alias(libs.plugins.ksp)                    apply false
}
