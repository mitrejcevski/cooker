package nl.jovmit.cooker.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository
import nl.jovmit.cooker.extensions.update
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.search.RecentRecipesResult
import nl.jovmit.cooker.model.search.SearchResult
import nl.jovmit.cooker.ui.search.state.SearchRecipeScreenState
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(
    private val searchRecipeRepository: SearchRecipeRepository,
    private val savedStateHandle: SavedStateHandle,
    private val backgroundDispatcher: CoroutineDispatcher
) : ViewModel() {

    val searchScreenState: StateFlow<SearchRecipeScreenState> =
        savedStateHandle.getStateFlow(SEARCH_SCREEN_KEY, SearchRecipeScreenState())

    fun updateQuery(newQuery: String) {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(query = newQuery)
        }
    }

    fun clearQuery() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(query = "", recipesFound = emptyList())
        }
    }

    fun loadRecentRecipes() {
        viewModelScope.launch {
            setLoading()
            withContext(backgroundDispatcher) {
                searchRecipeRepository.loadRecentRecipes().onEach { result ->
                    onRecentRecipesResult(result)
                }
            }.stateIn(viewModelScope)
        }
    }

    fun dismissLoadRecentRecipesError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isLoadingRecentRecipesError = false)
        }
    }

    fun findRecipesFor(query: String) {
        viewModelScope.launch {
            setLoading()
            val result = withContext(backgroundDispatcher) {
                searchRecipeRepository.searchRecipes(query, offset = 0)
            }
            onSearchResult(result)
        }
    }

    fun dismissSearchError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isSearchingError = false)
        }
    }

    fun dismissConnectionError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isConnectionError = false)
        }
    }

    fun loadMore(offset: Int) {
        val currentQuery = savedStateHandle
            .get<SearchRecipeScreenState>(SEARCH_SCREEN_KEY)?.query ?: return
        viewModelScope.launch {
            val result = withContext(backgroundDispatcher) {
                searchRecipeRepository.searchRecipes(currentQuery, offset)
            }
            onLoadMoreResult(result)
        }
    }

    private fun setLoading() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isLoading = true)
        }
    }

    private fun onRecentRecipesResult(result: RecentRecipesResult) {
        when (result) {
            is RecentRecipesResult.Recent -> setRecentRecipes(result.recipes)
            is RecentRecipesResult.LoadingError -> setLoadingRecentRecipesError()
        }
    }

    private fun setRecentRecipes(recipes: List<RecipeItem>) {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(recentRecipes = recipes, isLoading = false)
        }
    }

    private fun onSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.Matches -> setRecipesFound(result.recipeItems, result.canLoadMore)
            is SearchResult.BackendError -> setBackendError()
            is SearchResult.Offline -> setConnectionError()
        }
    }

    private fun setRecipesFound(recipeItems: List<RecipeItem>, canLoadMore: Boolean) {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(recipesFound = recipeItems, isLoading = false, canLoadMore = canLoadMore)
        }
    }

    private fun onLoadMoreResult(result: SearchResult) {
        when (result) {
            is SearchResult.Matches -> appendRecipesFound(result.recipeItems, result.canLoadMore)
            is SearchResult.BackendError -> setBackendError()
            is SearchResult.Offline -> setConnectionError()
        }
    }

    private fun appendRecipesFound(recipeItems: List<RecipeItem>, canLoadMore: Boolean) {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(recipesFound = it.recipesFound + recipeItems, canLoadMore = canLoadMore)
        }
    }

    private fun setLoadingRecentRecipesError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isLoadingRecentRecipesError = true, isLoading = false)
        }
    }

    private fun setBackendError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isSearchingError = true, isLoading = false, canLoadMore = false)
        }
    }

    private fun setConnectionError() {
        savedStateHandle.update<SearchRecipeScreenState>(SEARCH_SCREEN_KEY) {
            it.copy(isConnectionError = true, isLoading = false, canLoadMore = false)
        }
    }

    companion object {
        private const val SEARCH_SCREEN_KEY = "searchRecipeScreenKey"
    }
}