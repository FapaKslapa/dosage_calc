package com.example.dosagecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dosagecalc.presentation.navigation.AppNavigation
import com.example.dosagecalc.presentation.ui.theme.DosageCalcTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principale e unica dell'app (Single Activity Architecture).
 *
 * @AndroidEntryPoint abilita l'injection di Hilt in questa Activity
 * e in tutti i ViewModel raggiungibili dalla navigation.
 *
 * Responsabilità minima: setup del tema e avvio del NavHost.
 * Tutta la logica vive nel ViewModel e nei composable.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge: il contenuto si estende sotto status bar e navigation bar
        enableEdgeToEdge()
        setContent {
            DosageCalcTheme {
                // AppNavigation gestisce l'intero grafo di navigazione dell'app
                AppNavigation()
            }
        }
    }
}
