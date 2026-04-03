package com.example.dosagecalc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.calculator.screen.DosageResultScreen
import com.example.dosagecalc.presentation.calculator.screen.DrugSelectionScreen
import com.example.dosagecalc.presentation.calculator.screen.PatientInputScreen

/**
 * Rotte di navigazione dell'app.
 *
 * Usiamo un oggetto sealed per avere rotte type-safe: il compilatore
 * segnala immediatamente se una rotta viene rimossa o rinominata.
 */
sealed class AppRoute(val route: String) {
    object DrugSelection  : AppRoute("drug_selection")
    object PatientInput   : AppRoute("patient_input")
    object DosageResult   : AppRoute("dosage_result")
}

/**
 * NavHost principale dell'app.
 *
 * Il [CalculatorViewModel] viene creato UNA SOLA VOLTA a questo livello
 * e condiviso tra tutte e 3 le schermate: questo garantisce che lo stato
 * (farmaco selezionato, input paziente, risultato) sopravviva alla
 * navigazione tra schermate senza perdersi.
 *
 * Pattern: "hoisted ViewModel" passato a tutti i composable figli.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Il ViewModel vive per tutta la durata del NavHost (ciclo di vita Activity)
    val viewModel: CalculatorViewModel = hiltViewModel()

    NavHost(
        navController    = navController,
        startDestination = AppRoute.DrugSelection.route
    ) {

        composable(route = AppRoute.DrugSelection.route) {
            DrugSelectionScreen(
                viewModel   = viewModel,
                onNavigateToInput = {
                    navController.navigate(AppRoute.PatientInput.route)
                }
            )
        }

        composable(route = AppRoute.PatientInput.route) {
            PatientInputScreen(
                viewModel        = viewModel,
                onNavigateBack   = { navController.popBackStack() },
                onNavigateToResult = {
                    navController.navigate(AppRoute.DosageResult.route)
                }
            )
        }

        composable(route = AppRoute.DosageResult.route) {
            DosageResultScreen(
                viewModel      = viewModel,
                onNewCalculation = {
                    // Torna alla selezione e pulisce lo stack intermedio
                    navController.popBackStack(
                        route         = AppRoute.DrugSelection.route,
                        inclusive     = false
                    )
                    viewModel.resetCalculation()
                }
            )
        }
    }
}
