package nl.jovmit.cooker.ui.search.state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import nl.jovmit.cooker.model.RecipeItem

@Parcelize
@Stable
@Immutable
data class SearchRecipeScreenState(
    val isLoading: Boolean = false,
    val query: String = "",
    val recipesFound: List<RecipeItem> = emptyList(),
    val recentRecipes: List<RecipeItem> = emptyList(),
    val canLoadMore: Boolean = false,
    val isLoadingRecentRecipesError: Boolean = false,
    val isSearchingError: Boolean = false,
    val isConnectionError: Boolean = false
) : Parcelable