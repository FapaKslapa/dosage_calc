package com.example.dosagecalc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application personalizzata.
 *
 * @HiltAndroidApp è OBBLIGATORIO per Hilt: attiva la generazione del
 * component radice di Hilt e lo collega al ciclo di vita dell'Application.
 * Senza questa annotazione, nessuna injection funzionerà.
 *
 * Dichiarata nel AndroidManifest.xml con android:name=".DosageCalcApplication".
 */
@HiltAndroidApp
class DosageCalcApplication : Application()
