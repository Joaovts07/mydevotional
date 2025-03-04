package com.example.mydevotional

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mydevotional.components.BottomAppBarItem
import com.example.mydevotional.components.MyDevotionalBottomAppBar
import com.example.mydevotional.navigation.AppDestination
import com.example.mydevotional.navigation.bottomAppBarItems
import com.example.mydevotional.ui.theme.MyDevotionalTheme
import com.example.mydevotional.ui.theme.screens.HomeScreen
import com.example.mydevotional.ui.theme.screens.VersesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MyDevotionalTheme {
                AppNavigation(navController)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDevocionalScaffold(
    navController: NavController,
    selectedItem: BottomAppBarItem,
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Minha Leitura Diaria da Biblia") }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                MyDevotionalBottomAppBar(
                    item = selectedItem,
                    items = bottomAppBarItems,
                    onItemChange = { navController.navigate(it.destination.route) }
                )
            }

        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

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