package nl.jovmit.cooker.domain.recipe

import kotlinx.coroutines.flow.Flow
import nl.jovmit.cooker.model.details.RecipeResult

interface RecipeDetailsRepository {

    suspend fun fetchRecipeDetails(recipeId: Long): Flow<RecipeResult>

    suspend fun toggleFavorite(recipeId: Long)
}