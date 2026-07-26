package com.smartpantry.inventory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartpantry.inventory.presentation.screen.AddEditProductScreen
import com.smartpantry.inventory.presentation.screen.BarcodeScannerScreen
import com.smartpantry.inventory.presentation.screen.ProductDetailScreen
import com.smartpantry.inventory.presentation.screen.ProductListScreen
import com.smartpantry.inventory.presentation.screen.VirtualFridgeScreen

const val INVENTORY_ROUTE = "inventory"
const val PRODUCT_LIST_ROUTE = "list"
const val PRODUCT_DETAIL_ROUTE = "detail/{productId}"
const val ADD_PRODUCT_ROUTE = "add?barcode={barcode}"
const val ADD_PRODUCT_BASE_ROUTE = "add"
const val EDIT_PRODUCT_ROUTE = "edit/{productId}"
const val BARCODE_SCANNER_ROUTE = "scan"
const val VIRTUAL_FRIDGE_ROUTE = "virtual_fridge"

fun NavGraphBuilder.inventoryNavGraph(navController: NavHostController) {
    composable(PRODUCT_LIST_ROUTE) {
        ProductListScreen(
            viewModel = hiltViewModel(),
            onProductClick = { product ->
                navController.navigate("detail/$product")
            },
            onAddProduct = {
                navController.navigate(ADD_PRODUCT_BASE_ROUTE)
            },
            onScanBarcode = {
                navController.navigate(BARCODE_SCANNER_ROUTE)
            },
            onVirtualFridge = {
                navController.navigate(VIRTUAL_FRIDGE_ROUTE)
            }
        )
    }

    composable(
        route = PRODUCT_DETAIL_ROUTE,
        arguments = listOf(navArgument("productId") { type = NavType.LongType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
        ProductDetailScreen(
            viewModel = hiltViewModel(),
            productId = productId,
            onBack = { navController.popBackStack() },
            onEdit = { product ->
                navController.navigate("edit/${product.id}")
            },
            onDelete = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = ADD_PRODUCT_ROUTE,
        arguments = listOf(navArgument("barcode") { type = NavType.StringType; nullable = true; defaultValue = null })
    ) { backStackEntry ->
        val barcode = backStackEntry.arguments?.getString("barcode")
        AddEditProductScreen(
            viewModel = hiltViewModel(),
            onSave = { navController.popBackStack() },
            onCancel = { navController.popBackStack() },
            onBarcodeScan = {
                navController.navigate(BARCODE_SCANNER_ROUTE)
            },
            barcode = barcode
        )
    }

    composable(
        route = EDIT_PRODUCT_ROUTE,
        arguments = listOf(navArgument("productId") { type = NavType.LongType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
        AddEditProductScreen(
            viewModel = hiltViewModel(),
            onSave = { navController.popBackStack() },
            onCancel = { navController.popBackStack() },
            onBarcodeScan = {
                navController.navigate(BARCODE_SCANNER_ROUTE)
            },
            productId = productId
        )
    }

    composable(BARCODE_SCANNER_ROUTE) {
        BarcodeScannerScreen(
            viewModel = hiltViewModel(),
            onClose = { navController.popBackStack() },
            onScanResult = { barcode ->
                navController.navigate("add?barcode=$barcode") {
                    popUpTo(ADD_PRODUCT_BASE_ROUTE) { inclusive = true }
                }
            }
        )
    }

    composable(VIRTUAL_FRIDGE_ROUTE) {
        VirtualFridgeScreen(
            viewModel = hiltViewModel(),
            onBack = { navController.popBackStack() },
            onProductClick = { productId ->
                navController.navigate("detail/$productId")
            }
        )
    }
}
