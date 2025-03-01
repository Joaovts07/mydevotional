package com.example.mydevotional.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Star
import com.example.mydevotional.components.BottomAppBarItem

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("initialScreen")
    data object Account : AppDestination("account")
    data object FavoriteCourses: AppDestination("favoriteCourses")

}

val bottomAppBarItems = listOf(
    BottomAppBarItem(
        label = "Versiculos",
        icon = Icons.Filled.DateRange,
        destination = AppDestination.Home
    ),
    BottomAppBarItem(
        label = "Minha Conta",
        icon = Icons.Filled.AccountCircle,
        destination = AppDestination.Account
    ),
    BottomAppBarItem(
        label = "Favoritos",
        icon = Icons.Outlined.Star,
        destination = AppDestination.FavoriteCourses
    ),
)