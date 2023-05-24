package nl.jovmit.cooker.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import nl.jovmit.cooker.data.database.IngredientEntity
import nl.jovmit.cooker.data.database.InstructionEntity
import nl.jovmit.cooker.data.database.RecipeEntity
import nl.jovmit.cooker.data.database.RecipeWithInstructionsAndIngredients
import nl.jovmit.cooker.data.database.RecipesDao
import nl.jovmit.cooker.data.network.RecipesApi
import nl.jovmit.cooker.data.network.SearchResponse
import nl.jovmit.cooker.domain.recipe.RecentFirstSearchRecipeRepository
import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository
import nl.jovmit.cooker.model.RecipeItem
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RecentFirstSearchRecipeRepositoryTest : SearchRecipeRepositoryContractTest() {

    override fun searchRecipeRepositoryWith(
        recentRecipes: List<RecipeItem>,
        availableRecipes: List<RecipeItem>
    ): SearchRecipeRepository {
        val recipesApi = DummyRecipesApi(availableRecipes)
        val recipesDao = FakeRecipesDao(recentRecipes)
        return RecentFirstSearchRecipeRepository(recipesApi, recipesDao)
    }

    override fun searchRecipeRepositoryWithUnavailableRecipes(): SearchRecipeRepository {
        val recipesApi = DummyRecipesApi(recipes = null)
        val unavailableRecentRecipesDao = FakeRecipesDao(recentRecipes = null)
        return RecentFirstSearchRecipeRepository(recipesApi, unavailableRecentRecipesDao)
    }

    override fun offlineSearchRecipeRepository(): SearchRecipeRepository {
        val offlineApi = OfflineRecipesApi()
        val recipesDao = FakeRecipesDao(emptyList())
        return RecentFirstSearchRecipeRepository(offlineApi, recipesDao)
    }

    private class FakeRecipesDao(
        private val recentRecipes: List<RecipeItem>?
    ) : RecipesDao {

        override fun getRecentRecipes(): Flow<List<RecipeEntity>?> {
            return flowOf(recentRecipes?.map { recipeItem -> recipeItem.toRecipeEntity() })
        }

        override fun getRecipeFor(recipeId: Long): Flow<RecipeWithInstructionsAndIngredients?> {
            TODO("Not yet implemented")
        }

        override suspend fun toggleFavorite(recipeId: Long) {
            TODO("Not yet implemented")
        }

        override suspend fun insertRecipe(recipe: RecipeEntity) {
            TODO("Not yet implemented")
        }

        override suspend fun insertInstructions(instructions: List<InstructionEntity>) {
            TODO("Not yet implemented")
        }

        override suspend fun insertIngredients(ingredients: List<IngredientEntity>) {
            TODO("Not yet implemented")
        }

        private fun RecipeItem.toRecipeEntity(): RecipeEntity {
            return RecipeEntity(
                recipeId = id,
                title = title,
                image = image,
                summary = summary,
                readyInMinutes = readyInMinutes,
                isVegetarian = isVegetarian,
                isVegan = isVegan,
                healthScore = healthScore,
                servings = servings,
                sourceUrl = sourceUrl,
                isFavorite = isFavorite
            )
        }
    }

    private class DummyRecipesApi(
        private val recipes: List<RecipeItem>? = emptyList()
    ) : RecipesApi {

        override suspend fun searchRecipes(
            query: String,
            offset: Int,
            includeInformation: Boolean
        ): SearchResponse {
            if (recipes == null) {
                throw HttpException(Response.error<String>(400, "error".toResponseBody()))
            }
            val matchingItems = recipes.filter { it.title.contains(query, ignoreCase = true) }
            return SearchResponse(
                results = matchingItems.map { it.toRecipeResponse() },
                offset = offset,
                number = 10,
                totalResults = matchingItems.count()
            )
        }

        override suspend fun fetchRecipeDetails(recipeId: Long): SearchResponse.RecipeResponse {
            TODO("Not yet implemented")
        }

        private fun RecipeItem.toRecipeResponse(): SearchResponse.RecipeResponse {
            return SearchResponse.RecipeResponse(
                id = id,
                title = title,
                readyInMinutes = readyInMinutes,
                image = image,
                summary = summary,
                vegetarian = isVegetarian,
                vegan = isVegan,
                healthScore = healthScore,
                servings = servings,
                sourceUrl = sourceUrl,
                analyzedInstructions = emptyList(),
                extendedIngredients = emptyList()
            )
        }
    }

    private class OfflineRecipesApi : RecipesApi {
        override suspend fun searchRecipes(
            query: String,
            offset: Int,
            includeInformation: Boolean
        ): SearchResponse {
            throw IOException("connection error")
        }

        override suspend fun fetchRecipeDetails(
            recipeId: Long
        ): SearchResponse.RecipeResponse {
            throw IOException("connection error")
        }
    }
}
