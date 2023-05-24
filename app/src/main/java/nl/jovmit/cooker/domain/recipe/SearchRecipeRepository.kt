package nl.jovmit.cooker.domain.recipe

import kotlinx.coroutines.flow.Flow
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.search.RecentRecipesResult
import nl.jovmit.cooker.model.search.SearchResult

interface SearchRecipeRepository {

    fun loadRecentRecipes(): Flow<RecentRecipesResult>

    suspend fun searchRecipes(query: String, offset: Int): SearchResult
}