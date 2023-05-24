package nl.jovmit.cooker.ui.search

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nl.jovmit.cooker.CoroutinesTestExtension
import nl.jovmit.cooker.domain.InMemorySearchRecipeRepository
import nl.jovmit.cooker.extensions.observeFlow
import nl.jovmit.cooker.model.RecipeItemBuilder.Companion.aRecipeItem
import nl.jovmit.cooker.ui.search.state.SearchRecipeScreenState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class LoadRecentRecipesTest {

    private val pastaRecipe = aRecipeItem()
        .withId(1)
        .withTitle("Pasta")
        .build()
    private val fishRecipe = aRecipeItem()
        .withId(2)
        .withTitle("Fish")
        .build()
    private val recentRecipes = listOf(pastaRecipe, fishRecipe)

    private val searchRepository = InMemorySearchRecipeRepository()
    private val stateHandle = SavedStateHandle()
    private val backgroundDispatcher = Dispatchers.Unconfined

    @Test
    fun noRecentRecipes() {
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.loadRecentRecipes()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(recentRecipes = emptyList()))
    }

    @Test
    fun recentRecipesAvailable() {
        searchRepository.setRecentRecipes(recentRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.loadRecentRecipes()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(recentRecipes = recentRecipes))
    }

    @Test
    fun errorLoadingRecentRecipes() {
        val errorRepository = searchRepository.apply { setRecentRecipesUnavailable() }
        val viewModel = SearchRecipeViewModel(errorRepository, stateHandle, backgroundDispatcher)

        viewModel.loadRecentRecipes()

        assertThat(viewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(isLoading = false, isLoadingRecentRecipesError = true)
        )
    }

    @Test
    fun dismissRecentRecipesError() {
        val errorRepository = searchRepository.apply { setRecentRecipesUnavailable() }
        val viewModel =
            SearchRecipeViewModel(errorRepository, stateHandle, backgroundDispatcher).apply {
                loadRecentRecipes()
            }

        viewModel.dismissLoadRecentRecipesError()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(isLoadingRecentRecipesError = false))
    }

    @Test
    fun loadRecentRecipesScreenStatesDeliveredInOrder() = runTest {
        searchRepository.setRecentRecipes(recentRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        val deliveredStates = observeFlow(viewModel.searchScreenState) {
            viewModel.loadRecentRecipes()
        }

        assertThat(deliveredStates).isEqualTo(
            listOf(
                SearchRecipeScreenState(isLoading = true),
                SearchRecipeScreenState(recentRecipes = recentRecipes)
            )
        )
    }
}