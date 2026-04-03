# Regole ProGuard per DosageCalc
# Applicate solo nelle build di release (minifyEnabled = true)

# Mantiene le data class usate da kotlinx.serialization
# (il plugin serialization genera il codice corretto, ma per sicurezza
# preserviamo i nomi dei modelli di dominio e dei DTO)
-keepclassmembers class com.example.dosagecalc.data.model.** {
    *;
}

# Hilt genera classi a compile-time: non servono regole extra
# perché usa KSP (non reflection runtime).

# Regola standard per le coroutine
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
