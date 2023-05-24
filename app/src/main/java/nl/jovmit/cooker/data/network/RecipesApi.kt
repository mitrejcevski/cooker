package nl.jovmit.cooker.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("offset") offset: Int = 0,
        @Query("addRecipeInformation") includeInformation: Boolean = true
    ): SearchResponse

    @GET("recipes/{recipeId}/information")
    suspend fun fetchRecipeDetails(
        @Path("recipeId") recipeId: Long
    ) : SearchResponse.RecipeResponse
}
