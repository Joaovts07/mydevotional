package com.example.mydevotional

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.login.login.LoginState
import com.example.login.login.LoginViewModel
import com.example.login.ui.screens.LoginNavigation
import com.example.mydevotional.components.BottomAppBarItem
import com.example.mydevotional.components.MyDevotionalBottomAppBar
import com.example.mydevotional.navigation.AppDestination
import com.example.mydevotional.navigation.AppNavigation
import com.example.mydevotional.navigation.bottomAppBarItems
import com.example.mydevotional.ui.theme.MyDevotionalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDevotionalTheme {
                InitNavigation()
            }

        }
    }
    @Composable
    fun InitNavigation(
        loginViewModel: LoginViewModel = hiltViewModel()
    ) {
        val navController = rememberNavController()
        val loginState by loginViewModel.loginState.collectAsState()

        when (loginState) {
            is LoginState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoginState.Logged -> {
                AppNavigation(navController)
            }

            is LoginState.Logout, LoginState.Idle -> {
                LoginNavigation(
                    navController = navController,
                    routeSuccess = AppDestination.Account.route
                )
            }

            is LoginState.Error -> {
                LoginNavigation(
                    navController = navController,
                    routeSuccess = AppDestination.Account.route)
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

