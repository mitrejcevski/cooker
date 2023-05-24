package nl.jovmit.cooker.ui.details

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import nl.jovmit.cooker.CoroutinesTestExtension
import nl.jovmit.cooker.domain.InMemoryRecipeDetailsRepository
import nl.jovmit.cooker.extensions.observeFlow
import nl.jovmit.cooker.model.RecipeItemBuilder
import nl.jovmit.cooker.ui.details.state.RecipeDetailsScreenState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class ToggleRecipeFavoriteTest {

    private val pastaWithShrimps = RecipeItemBuilder.aRecipeItem()
        .withId(1111)
        .withTitle("Pasta with Shrimps")
        .build()

    private val detailsRepository = InMemoryRecipeDetailsRepository()
    private val stateHandle = SavedStateHandle()
    private val dispatcher = Dispatchers.Unconfined

    @BeforeEach
    fun setUp() {
        detailsRepository.setAvailableRecipes(listOf(pastaWithShrimps))
    }

    @Test
    fun toggleFavoriteRecipe() = runTest {
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        val recipeDetailsUpdates = observeFlow(viewModel.recipeDetailsScreenState) {
            viewModel.loadRecipeDetails(pastaWithShrimps.id)
            viewModel.toggleFavorite(pastaWithShrimps.id)
        }

        assertThat(recipeDetailsUpdates).isEqualTo(
            listOf(
                RecipeDetailsScreenState(isLoading = true),
                RecipeDetailsScreenState(recipeDetails = pastaWithShrimps),
                RecipeDetailsScreenState(recipeDetails = pastaWithShrimps.copy(isFavorite = true))
            )
        )
    }
}