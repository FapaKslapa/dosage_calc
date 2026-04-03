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
import com.example.dosagecalc.presentation.history.HistoryViewModel
import com.example.dosagecalc.presentation.history.screen.HistoryScreen
import com.example.dosagecalc.presentation.patient.PatientsViewModel
import com.example.dosagecalc.presentation.patient.screen.PatientsScreen
import com.example.dosagecalc.presentation.calculator.RemindersViewModel
import com.example.dosagecalc.presentation.calculator.screen.RemindersScreen

sealed class AppRoute(val route: String) {
    object DrugSelection  : AppRoute("drug_selection")
    object PatientInput   : AppRoute("patient_input")
    object DosageResult   : AppRoute("dosage_result")
    object PatientsList   : AppRoute("patients_list")
    object GlobalHistory  : AppRoute("global_history")
    object Reminders      : AppRoute("reminders")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    
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
                },
                onNavigateToPatients = {
                    navController.navigate(AppRoute.PatientsList.route)
                },
                onNavigateToHistory = {
                    navController.navigate(AppRoute.GlobalHistory.route)
                },
                onNavigateToReminders = {
                    navController.navigate(AppRoute.Reminders.route)
                }
            )
        }

        composable(route = AppRoute.PatientsList.route) {
            val patientsViewModel: PatientsViewModel = hiltViewModel()
            PatientsScreen(
                viewModel = patientsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = { patientId ->
                    
                    navController.navigate(AppRoute.GlobalHistory.route)
                }
            )
        }

        composable(route = AppRoute.GlobalHistory.route) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = AppRoute.Reminders.route) {
            val remindersViewModel: RemindersViewModel = hiltViewModel()
            RemindersScreen(
                viewModel = remindersViewModel,
                onNavigateBack = { navController.popBackStack() }
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
