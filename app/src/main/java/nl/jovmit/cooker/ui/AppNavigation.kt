package nl.jovmit.cooker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.jovmit.cooker.ui.details.RecipeDetailsScreen
import nl.jovmit.cooker.ui.search.SearchRecipeScreen
import nl.jovmit.cooker.ui.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destinations.Search.route
    ) {
        composable(Destinations.Search.route) {
            SearchRecipeScreen(
                onRecipeClick = { recipeId ->
                    val route = Destinations.RecipeDetails.createRoute(recipeId)
                    navController.navigate(route)
                },
                onSettingsClick = {
                    navController.navigate(Destinations.Settings.route)
                }
            )
        }
        composable(Destinations.RecipeDetails.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            requireNotNull(recipeId) { "The recipe id is mandatory" }
            RecipeDetailsScreen(
                recipeId = recipeId.toLong(),
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(Destinations.Settings.route) {
            SettingsScreen(
                settingsViewModel = viewModel(LocalContext.current as MainActivity),
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}