package nl.jovmit.cooker.model.search

import nl.jovmit.cooker.model.RecipeItem

sealed class SearchResult {

    data class Matches(
        val recipeItems: List<RecipeItem>,
        val canLoadMore: Boolean
    ): SearchResult()

    object BackendError: SearchResult()

    object Offline: SearchResult()
}
