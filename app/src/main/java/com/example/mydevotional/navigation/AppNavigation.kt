package com.example.mydevotional.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mydevotional.MyDevocionalScaffold
import com.example.mydevotional.ui.screens.BooksScreen
import com.example.mydevotional.ui.screens.ChaptersScreen
import com.example.mydevotional.ui.screens.HomeScreen
import com.example.mydevotional.ui.screens.VersesScreen
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntryState?.destination
    val versesViewModel: VersesViewModel = hiltViewModel()
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
        composable(AppDestination.BibleBooks.route) {
            MyDevocionalScaffold(navController, selectedItem) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    BooksScreen(navController, versesViewModel)
                }
            }
        }
        composable("${AppDestination.BibleChapters.route}/{bookName}") { backStackEntry ->
            MyDevocionalScaffold(navController, selectedItem) { paddingValues ->
                val bookName = backStackEntry.arguments?.getString("bookName") ?: ""
                Box(modifier = Modifier.padding(paddingValues)) {
                    ChaptersScreen(navController, bookName, versesViewModel)
                }
            }
        }

        composable(AppDestination.BibleVerses.route) { backStackEntry ->
            MyDevocionalScaffold(navController, selectedItem) { paddingValues ->
                val bookName = backStackEntry.arguments?.getString("bookName") ?: ""
                val chapter = backStackEntry.arguments?.getInt("chapter") ?: 1
                Box(modifier = Modifier.padding(paddingValues)) {
                    VersesScreen(bookName, chapter, versesViewModel)
                }
            }
        }

    }
}