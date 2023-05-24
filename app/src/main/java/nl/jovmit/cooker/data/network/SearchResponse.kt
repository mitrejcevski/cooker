package nl.jovmit.cooker.data.network

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val results: List<RecipeResponse>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
) {

    @Serializable
    data class RecipeResponse(
        val id: Long,
        val title: String,
        val readyInMinutes: Int,
        val image: String,
        val summary: String,
        val vegetarian: Boolean,
        val vegan: Boolean,
        val healthScore: Int,
        val servings: Int,
        val sourceUrl: String,
        val analyzedInstructions: List<Instruction>,
        val extendedIngredients: List<Ingredient>? = null
    ) {

        @Serializable
        data class Instruction(
            val steps: List<Step>
        ) {

            @Serializable
            data class Step(
                val number: Int,
                val step: String
            )
        }

        @Serializable
        data class Ingredient(
            val id: Long,
            val name: String,
            val image: String,
            val amount: Double,
            val unit: String
        )
    }
}