package com.smartpantry.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.smartpantry.inventory.presentation.navigation.PRODUCT_LIST_ROUTE
import com.smartpantry.inventory.presentation.navigation.inventoryNavGraph

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = PRODUCT_LIST_ROUTE) {
        inventoryNavGraph(navController)
    }
}
