package nl.jovmit.cooker.ui.search

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.*
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
class SearchRecipesTest {

    private val pastaKeyword = "pasta"
    private val pestoKeyword = "pesto"
    private val pastaWithTomatoes = aRecipeItem()
        .withTitle("Pasta with tomatoes")
        .build()
    private val pastaWithMushrooms = aRecipeItem()
        .withTitle("Mushrooms pasta")
        .build()
    private val basilPesto = aRecipeItem()
        .withTitle("Basil Pesto")
        .build()
    private val tomatoPesto = aRecipeItem()
        .withTitle("Pesto from dried tomatoes")
        .build()
    private val pestoRecipes = listOf(basilPesto, tomatoPesto)
    private val pastaRecipes = listOf(pastaWithTomatoes, pastaWithMushrooms)
    private val allRecipes = listOf(pastaWithTomatoes, pastaWithMushrooms, basilPesto, tomatoPesto)

    private val searchRepository = InMemorySearchRecipeRepository()
    private val stateHandle = SavedStateHandle()
    private val backgroundDispatcher = Dispatchers.Unconfined

    @Test
    fun noRecipesFoundForQuery() {
        searchRepository.setAvailableRecipes(pestoRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.findRecipesFor(pastaKeyword)

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(recipesFound = emptyList()))
    }

    @Test
    fun recipesFoundForQuery() {
        searchRepository.setAvailableRecipes(allRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.findRecipesFor(pastaKeyword)

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(recipesFound = pastaRecipes))
    }

    @Test
    fun clearingQueryClearsMatchingRecipes() {
        searchRepository.setAvailableRecipes(allRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher).apply {
            findRecipesFor(pastaKeyword)
        }

        viewModel.clearQuery()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(recipesFound = emptyList()))
    }

    @Test
    fun searchingRecipesBackendError() {
        searchRepository.setNoRecipesAvailable()
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.findRecipesFor(":irrelevant:")

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(isSearchingError = true))
    }

    @Test
    fun dismissBackendError() {
        searchRepository.setNoRecipesAvailable()
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher).apply {
            findRecipesFor(":irrelevant:")
        }

        viewModel.dismissSearchError()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(isSearchingError = false))
    }

    @Test
    fun searchingWhileOffline() {
        searchRepository.setOffline()
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.findRecipesFor(":irrelevant:")

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(isConnectionError = true))
    }

    @Test
    fun dismissOfflineError() {
        searchRepository.setOffline()
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher).apply {
            findRecipesFor(":irrelevant:")
        }

        viewModel.dismissConnectionError()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(isConnectionError = false))
    }

    @Test
    fun searchingRecipesScreenStatesDeliveredInOrder() = runTest {
        searchRepository.setAvailableRecipes(allRecipes)
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        val deliveredStates = observeFlow(viewModel.searchScreenState) {
            viewModel.findRecipesFor(pestoKeyword)
        }

        assertThat(deliveredStates).isEqualTo(
            listOf(
                SearchRecipeScreenState(isLoading = true),
                SearchRecipeScreenState(recipesFound = pestoRecipes)
            )
        )
    }
}