package com.smartpantry.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartpantry.inventory.presentation.navigation.InventoryNavHost

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "list") {
        composable("list") {
            InventoryNavHost()
        }
    }
}