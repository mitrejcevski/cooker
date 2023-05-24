package nl.jovmit.cooker.ui.details

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nl.jovmit.cooker.CoroutinesTestExtension
import nl.jovmit.cooker.domain.InMemoryRecipeDetailsRepository
import nl.jovmit.cooker.extensions.observeFlow
import nl.jovmit.cooker.model.RecipeItemBuilder.Companion.aRecipeItem
import nl.jovmit.cooker.ui.details.state.RecipeDetailsScreenState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class LoadRecipeDetailsTest {

    private val pastaWithMushrooms = aRecipeItem()
        .withId(1234)
        .withTitle("Pasta with Mushrooms")
        .build()
    private val pastaWithCheese = aRecipeItem()
        .withId(5678)
        .withTitle("Pasta with Cheese")
        .build()
    private val availableRecipes = listOf(pastaWithMushrooms, pastaWithCheese)

    private val detailsRepository = InMemoryRecipeDetailsRepository()
    private val stateHandle = SavedStateHandle()
    private val dispatcher = Dispatchers.Unconfined

    @BeforeEach
    fun setUp() {
        detailsRepository.setAvailableRecipes(availableRecipes)
    }

    @Test
    fun loadRecipeDetails() {
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(pastaWithMushrooms.id)

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(recipeDetails = pastaWithMushrooms))
    }

    @Test
    fun recipeNotFound() {
        val nonExistingRecipeId = -1L
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(nonExistingRecipeId)

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(isRecipeNotFoundError = true))
    }

    @Test
    fun errorLoadingRecipe() {
        detailsRepository.setRecipesUnavailable()
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(pastaWithCheese.id)

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(isBackendError = true))
    }

    @Test
    fun errorLoadingRemoteRecipeAvailableInCache() {
        detailsRepository.apply {
            setRecipesUnavailable()
            setOfflineRecipes(availableRecipes)
        }
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(pastaWithMushrooms.id)

        assertThat(viewModel.recipeDetailsScreenState.value).isEqualTo(
            RecipeDetailsScreenState(isBackendError = true, recipeDetails = pastaWithMushrooms)
        )
    }

    @Test
    fun dismissBackendError() {
        detailsRepository.apply {
            setRecipesUnavailable()
            setOfflineRecipes(availableRecipes)
        }
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher).apply {
            loadRecipeDetails(pastaWithMushrooms.id)
        }

        viewModel.dismissBackendError()

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(recipeDetails = pastaWithMushrooms))
    }

    @Test
    fun loadingRecipeWhileOffline() {
        val irrelevantRecipeId = 123L
        detailsRepository.setOffline()
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(irrelevantRecipeId)

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(isConnectionError = true))
    }

    @Test
    fun loadingCachedRecipeWhileOffline() {
        detailsRepository.apply {
            setOffline()
            setOfflineRecipes(availableRecipes)
        }
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        viewModel.loadRecipeDetails(pastaWithCheese.id)

        assertThat(viewModel.recipeDetailsScreenState.value).isEqualTo(
            RecipeDetailsScreenState(isConnectionError = true, recipeDetails = pastaWithCheese)
        )
    }

    @Test
    fun dismissOfflineError() {
        detailsRepository.apply {
            setOffline()
            setOfflineRecipes(availableRecipes)
        }
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher).apply {
            loadRecipeDetails(pastaWithCheese.id)
        }

        viewModel.dismissConnectionError()

        assertThat(viewModel.recipeDetailsScreenState.value)
            .isEqualTo(RecipeDetailsScreenState(recipeDetails = pastaWithCheese))
    }

    @Test
    fun loadingRecipeDetailsScreenStatesDeliveredInOrder() = runTest {
        val viewModel = RecipeDetailsViewModel(detailsRepository, stateHandle, dispatcher)

        val deliveredStates = observeFlow(viewModel.recipeDetailsScreenState) {
            viewModel.loadRecipeDetails(pastaWithCheese.id)
        }

        assertThat(deliveredStates).isEqualTo(
            listOf(
                RecipeDetailsScreenState(isLoading = true),
                RecipeDetailsScreenState(isLoading = false, recipeDetails = pastaWithCheese)
            )
        )
    }
}