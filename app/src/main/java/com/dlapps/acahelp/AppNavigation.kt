package com.dlapps.acahelp

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.dlapps.acahelp.repositories.AuthRepository
import com.dlapps.acahelp.screens.LoginScreen
import com.dlapps.acahelp.screens.QuestionListScreen
import com.dlapps.acahelp.screens.RegisterScreen

@Composable
fun AppNavigation(startDestination:String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        navigation(startDestination="login", route = "auth_graph"){
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
        }
        navigation(startDestination = "home", route = "main_graph"){
            composable ("home") { QuestionListScreen(navController) }
        }

    }
}