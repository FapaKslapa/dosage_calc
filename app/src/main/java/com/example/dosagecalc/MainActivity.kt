package com.example.dosagecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.presentation.main.MainViewModel
import com.example.dosagecalc.presentation.navigation.AppNavigation
import com.example.dosagecalc.presentation.ui.theme.DosageCalcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by mainViewModel.isDarkTheme.collectAsStateWithLifecycle()
            DosageCalcTheme(darkTheme = isDark) {
                AppNavigation(onToggleTheme = mainViewModel::toggleTheme)
            }
        }
    }
}
