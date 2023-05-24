package nl.jovmit.cooker.model.search

import nl.jovmit.cooker.model.RecipeItem

sealed class RecentRecipesResult {

    data class Recent(val recipes: List<RecipeItem>) : RecentRecipesResult()

    object LoadingError : RecentRecipesResult()
}