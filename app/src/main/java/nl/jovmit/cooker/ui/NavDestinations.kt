package nl.jovmit.cooker.ui

sealed class Destinations(val route: String) {

    object Search : Destinations("search")

    object RecipeDetails : Destinations("recipe/{recipeId}") {

        fun createRoute(recipeId: Long) = "recipe/$recipeId"
    }

    object Settings : Destinations("settings")
}