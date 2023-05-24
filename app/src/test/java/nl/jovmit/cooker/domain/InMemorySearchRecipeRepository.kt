package nl.jovmit.cooker.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.search.RecentRecipesResult
import nl.jovmit.cooker.model.search.SearchResult

class InMemorySearchRecipeRepository(
    private val defaultPageSize: Int = 10
) : SearchRecipeRepository {

    private var isOffline = false
    private var recentRecipes: List<RecipeItem>? = emptyList()
    private var availableRecipes: List<RecipeItem>? = emptyList()

    override fun loadRecentRecipes(): Flow<RecentRecipesResult> {
        val result = when (val recipes = recentRecipes) {
            null -> RecentRecipesResult.LoadingError
            else -> RecentRecipesResult.Recent(recipes)
        }
        return flowOf(result)
    }

    override suspend fun searchRecipes(query: String, offset: Int): SearchResult {
        if (isOffline) return SearchResult.Offline
        return when (val recipes = availableRecipes) {
            null -> SearchResult.BackendError
            else -> resultWithMatches(recipes, query, offset)
        }
    }

    private fun resultWithMatches(
        recipes: List<RecipeItem>,
        query: String,
        offset: Int
    ): SearchResult.Matches {
        val matches = recipes.filter { it.title.contains(query, ignoreCase = true) }
        val askedPage = matches.drop(offset).take(defaultPageSize)
        val canLoadMore = matches.size > offset + defaultPageSize
        return SearchResult.Matches(askedPage, canLoadMore)
    }

    fun setRecentRecipes(recentRecipes: List<RecipeItem>) {
        this.recentRecipes = recentRecipes
    }

    fun setRecentRecipesUnavailable() {
        this.recentRecipes = null
    }

    fun setAvailableRecipes(recipes: List<RecipeItem>) {
        this.availableRecipes = recipes
    }

    fun setNoRecipesAvailable() {
        this.availableRecipes = null
    }

    fun setOffline() {
        this.isOffline = true
    }
}