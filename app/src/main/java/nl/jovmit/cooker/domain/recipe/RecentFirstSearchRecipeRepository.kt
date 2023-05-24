package nl.jovmit.cooker.domain.recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.jovmit.cooker.data.database.RecipeEntity
import nl.jovmit.cooker.data.database.RecipesDao
import nl.jovmit.cooker.data.network.RecipesApi
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.search.RecentRecipesResult
import nl.jovmit.cooker.model.search.SearchResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RecentFirstSearchRecipeRepository @Inject constructor(
    private val recipesApi: RecipesApi,
    private val recipesDao: RecipesDao
) : SearchRecipeRepository {
    
    private val mapper = RecipeItemMapper()

    override fun loadRecentRecipes(): Flow<RecentRecipesResult> {
        return recipesDao.getRecentRecipes().map { recipeEntities ->
            when (recipeEntities) {
                null -> RecentRecipesResult.LoadingError
                else -> RecentRecipesResult.Recent(recipeEntities.toRecipes())
            }
        }
    }

    override suspend fun searchRecipes(query: String, offset: Int): SearchResult {
        return try {
            val response = recipesApi.searchRecipes(query, offset)
            val recipeItems = response.results.map { recipeResponse -> 
                mapper.recipeResponseToRecipeItem(recipeResponse) 
            }
            val canLoadMore = response.totalResults > response.number + offset
            SearchResult.Matches(recipeItems, canLoadMore)
        } catch (httpException: HttpException) {
            SearchResult.BackendError
        } catch (ioException: IOException) {
            SearchResult.Offline
        }
    }

    private fun List<RecipeEntity>.toRecipes(): List<RecipeItem> {
        return this.map { entity -> mapper.recipeEntityToRecipeItem(entity) }
    }
}