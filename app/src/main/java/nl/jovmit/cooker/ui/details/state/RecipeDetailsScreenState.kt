package nl.jovmit.cooker.ui.details.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import nl.jovmit.cooker.model.RecipeItem

@Parcelize
data class RecipeDetailsScreenState(
    val isLoading: Boolean = false,
    val isRecipeNotFoundError: Boolean = false,
    val isBackendError: Boolean = false,
    val isConnectionError: Boolean = false,
    val recipeDetails: RecipeItem? = null,
): Parcelable