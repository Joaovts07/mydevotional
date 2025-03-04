package com.example.mydevotional.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mydevotional.MyDevocionalScaffold
import com.example.mydevotional.ui.theme.screens.HomeScreen
import com.example.mydevotional.ui.theme.screens.VersesScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntryState?.destination
    val selectedItem by remember(currentDestination) {
        mutableStateOf(
            bottomAppBarItems.find { it.destination.route == currentDestination?.route }
                ?: bottomAppBarItems.first()
        )
    }
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            MyDevocionalScaffold(navController, selectedItem) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    HomeScreen()
                }
            }
        }
        composable(AppDestination.BibleVerses.route) {
            MyDevocionalScaffold(navController, selectedItem) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    VersesScreen()
                }
            }
        }

    }
}