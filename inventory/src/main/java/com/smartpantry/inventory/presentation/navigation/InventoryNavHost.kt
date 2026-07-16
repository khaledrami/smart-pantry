package com.smartpantry.inventory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartpantry.inventory.presentation.screen.ProductListScreen

@Composable
fun InventoryNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "list") {
        composable("list") {
            ProductListScreen()
        }
    }
}