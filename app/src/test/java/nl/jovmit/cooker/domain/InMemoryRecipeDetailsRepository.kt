package nl.jovmit.cooker.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import nl.jovmit.cooker.domain.recipe.RecipeDetailsRepository
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.details.RecipeResult

class InMemoryRecipeDetailsRepository : RecipeDetailsRepository {

    private var isOffline = false
    private var availableRecipes: MutableList<RecipeItem>? = null
    private var offlineRecipes: MutableList<RecipeItem> = arrayListOf()

    private val recipeDetailsFlow = MutableStateFlow<RecipeResult>(RecipeResult.Offline)

    override suspend fun fetchRecipeDetails(recipeId: Long): Flow<RecipeResult> {
        if (isOffline) {
            val result = offlineRecipeResultFor(recipeId)
            recipeDetailsFlow.update { result }
        } else {
            val result = when (val recipes = availableRecipes) {
                null -> backendErrorRecipeResultFor(recipeId)
                else -> recipeResult(recipes, recipeId)
            }
            recipeDetailsFlow.update { result }
        }
        return recipeDetailsFlow
    }

    private fun offlineRecipeResultFor(recipeId: Long): RecipeResult {
        val matchInCache = offlineRecipes.find { it.id == recipeId }
        return if (matchInCache != null) {
            RecipeResult.Recipe(matchInCache, isOffline = true)
        } else {
            RecipeResult.Offline
        }
    }

    private fun backendErrorRecipeResultFor(recipeId: Long): RecipeResult {
        val matchInCache = offlineRecipes.find { it.id == recipeId }
        return if (matchInCache != null) {
            RecipeResult.Recipe(matchInCache, isBackendError = true)
        } else {
            RecipeResult.BackendError
        }
    }

    private fun recipeResult(
        recipes: List<RecipeItem>,
        recipeId: Long
    ): RecipeResult {
        val match = recipes.find { it.id == recipeId }
        return if (match != null) {
            RecipeResult.Recipe(match)
        } else {
            RecipeResult.NotFoundError
        }
    }

    override suspend fun toggleFavorite(recipeId: Long) {
        val currentValue = recipeDetailsFlow.value
        if (currentValue is RecipeResult.Recipe && currentValue.value.id == recipeId) {
            val recipe = currentValue.value
            val toggled = recipe.copy(isFavorite = !recipe.isFavorite)
            recipeDetailsFlow.update { currentValue.copy(value = toggled) }
        }
    }

    fun setAvailableRecipes(recipes: List<RecipeItem>) {
        this.availableRecipes = recipes.toMutableList()
    }

    fun setOfflineRecipes(offlineRecipes: List<RecipeItem>) {
        this.offlineRecipes = offlineRecipes.toMutableList()
    }

    fun setRecipesUnavailable() {
        this.availableRecipes = null
    }

    fun setOffline() {
        this.isOffline = true
    }
}