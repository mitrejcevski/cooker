package nl.jovmit.cooker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeItem(
    val id: Long,
    val title: String,
    val readyInMinutes: Int,
    val image: String,
    val summary: String,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val healthScore: Int,
    val servings: Int,
    val sourceUrl: String,
    val isFavorite: Boolean = false,
    val instructions: List<Instruction> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
): Parcelable {

    @Parcelize
    data class Instruction(
        val number: Int,
        val step: String
    ): Parcelable

    @Parcelize
    data class Ingredient(
        val name: String,
        val image: String,
        val amount: Double,
        val unit: String
    ): Parcelable
}