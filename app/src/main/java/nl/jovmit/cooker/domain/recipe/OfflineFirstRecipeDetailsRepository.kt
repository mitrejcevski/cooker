package nl.jovmit.cooker.domain.recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.jovmit.cooker.data.database.IngredientEntity
import nl.jovmit.cooker.data.database.InstructionEntity
import nl.jovmit.cooker.data.database.RecipesDao
import nl.jovmit.cooker.data.network.RecipesApi
import nl.jovmit.cooker.data.network.RemoteRecipeResult
import nl.jovmit.cooker.data.network.SearchResponse
import nl.jovmit.cooker.data.network.SearchResponse.RecipeResponse.Ingredient
import nl.jovmit.cooker.data.network.SearchResponse.RecipeResponse.Instruction.Step
import nl.jovmit.cooker.model.details.RecipeResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OfflineFirstRecipeDetailsRepository @Inject constructor(
    private val recipesApi: RecipesApi,
    private val recipesDao: RecipesDao
) : RecipeDetailsRepository {

    private val mapper = RecipeItemMapper()

    override suspend fun fetchRecipeDetails(recipeId: Long): Flow<RecipeResult> {
        return when (val result = loadRemoteRecipe(recipeId)) {
            is RemoteRecipeResult.RecipeValue ->
                updatedRecipeResult(result.value)

            is RemoteRecipeResult.BackendError ->
                cachedRecipeResult(recipeId = recipeId, backendError = true)

            is RemoteRecipeResult.ConnectionError ->
                cachedRecipeResult(recipeId = recipeId, connectionError = true)
        }
    }

    override suspend fun toggleFavorite(recipeId: Long) {
        recipesDao.toggleFavorite(recipeId)
    }

    private fun cachedRecipeResult(
        recipeId: Long,
        backendError: Boolean = false,
        connectionError: Boolean = false
    ): Flow<RecipeResult> {
        return recipesDao.getRecipeFor(recipeId).map { recipe ->
            when (recipe) {
                null -> errorRecipeResult(backendError, connectionError)
                else -> RecipeResult.Recipe(
                    value = mapper.fullRecipeEntityToRecipeItem(recipe),
                    isBackendError = backendError,
                    isOffline = connectionError
                )
            }
        }
    }

    private fun errorRecipeResult(backendError: Boolean, connectionError: Boolean): RecipeResult {
        return if (backendError) RecipeResult.NotFoundError
        else if (connectionError) RecipeResult.Offline
        else RecipeResult.NotFoundError
    }

    private suspend fun updatedRecipeResult(
        response: SearchResponse.RecipeResponse
    ): Flow<RecipeResult> {
        storeRecipeLocally(response)
        return recipesDao.getRecipeFor(response.id).map { recipe ->
            if (recipe == null) RecipeResult.NotFoundError
            else RecipeResult.Recipe(mapper.fullRecipeEntityToRecipeItem(recipe))
        }
    }

    private suspend fun storeRecipeLocally(response: SearchResponse.RecipeResponse) {
        val recipe = mapper.recipeResponseToRecipeEntity(response)
        val instructions = response.analyzedInstructions
            .flatMap { it.steps }
            .toInstructionEntities(response.id)
        val ingredients = response.extendedIngredients
            .orEmpty()
            .toIngredientEntities(response.id)

        recipesDao.insertRecipe(recipe)
        recipesDao.insertInstructions(instructions)
        recipesDao.insertIngredients(ingredients)
    }

    private suspend fun loadRemoteRecipe(recipeId: Long): RemoteRecipeResult {
        return try {
            val response = recipesApi.fetchRecipeDetails(recipeId)
            RemoteRecipeResult.RecipeValue(response)
        } catch (httpException: HttpException) {
            RemoteRecipeResult.BackendError
        } catch (connectionException: IOException) {
            RemoteRecipeResult.ConnectionError
        }
    }

    private fun List<Step>.toInstructionEntities(recipeId: Long): List<InstructionEntity> {
        return this.map { step ->
            mapper.instructionStepToInstructionEntity(recipeId, step)
        }
    }

    private fun List<Ingredient>.toIngredientEntities(recipeId: Long): List<IngredientEntity> {
        return this.map { ingredient ->
            mapper.ingredientResponseToIngredientEntity(recipeId, ingredient)
        }
    }
}