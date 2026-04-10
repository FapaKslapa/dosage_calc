package com.example.dosagecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.presentation.main.MainViewModel
import com.example.dosagecalc.presentation.navigation.AppNavigation
import com.example.dosagecalc.presentation.ui.theme.DosageCalcTheme
import com.example.dosagecalc.presentation.ui.util.LocalWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by mainViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)
            DosageCalcTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    AppNavigation(onToggleTheme = mainViewModel::toggleTheme)
                }
            }
        }
    }
}
