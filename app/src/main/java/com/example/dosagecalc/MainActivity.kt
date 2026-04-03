package com.example.dosagecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dosagecalc.presentation.navigation.AppNavigation
import com.example.dosagecalc.presentation.ui.theme.DosageCalcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            DosageCalcTheme {
                
                AppNavigation()
            }
        }
    }
}
