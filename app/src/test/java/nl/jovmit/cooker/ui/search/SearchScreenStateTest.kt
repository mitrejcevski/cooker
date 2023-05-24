package nl.jovmit.cooker.ui.search

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import nl.jovmit.cooker.domain.InMemorySearchRecipeRepository
import nl.jovmit.cooker.ui.search.state.SearchRecipeScreenState
import org.junit.jupiter.api.Test

class SearchScreenStateTest {

    private val searchRepository = InMemorySearchRecipeRepository()
    private val stateHandle = SavedStateHandle()
    private val backgroundDispatcher = Dispatchers.Unconfined

    @Test
    fun defaultScreenState() {
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState())
    }

    @Test
    fun updateQuery() {
        val newQuery = ":irrelevant:"
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)

        viewModel.updateQuery(newQuery)

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(query = newQuery))
    }

    @Test
    fun clearQuery() {
        val viewModel = SearchRecipeViewModel(searchRepository, stateHandle, backgroundDispatcher)
        viewModel.updateQuery("some query")

        viewModel.clearQuery()

        assertThat(viewModel.searchScreenState.value)
            .isEqualTo(SearchRecipeScreenState(query = ""))
    }
}