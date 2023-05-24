package nl.jovmit.cooker.ui.search

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import nl.jovmit.cooker.CoroutinesTestExtension
import nl.jovmit.cooker.domain.InMemorySearchRecipeRepository
import nl.jovmit.cooker.model.RecipeItemBuilder
import nl.jovmit.cooker.ui.search.state.SearchRecipeScreenState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class SearchRecipesPagingTest {

    private val pastaKeyword = "pasta"
    private val pastaWithTomatoes = RecipeItemBuilder.aRecipeItem()
        .withTitle("Pasta with tomatoes")
        .build()
    private val pastaWithMushrooms = RecipeItemBuilder.aRecipeItem()
        .withTitle("Mushrooms pasta")
        .build()
    private val pastaWithShrimps = RecipeItemBuilder.aRecipeItem()
        .withTitle("Spaghetti pasta with shrimps")
        .build()
    private val pastaWithMeatBalls = RecipeItemBuilder.aRecipeItem()
        .withTitle("Meat balls pasta")
        .build()
    private val allRecipes =
        listOf(pastaWithTomatoes, pastaWithMushrooms, pastaWithShrimps, pastaWithMeatBalls)

    private val searchRepository = InMemorySearchRecipeRepository(defaultPageSize = 1)
    private val stateHandle = SavedStateHandle()
    private val dispatcher = Dispatchers.Unconfined
    private val searchRecipeViewModel = SearchRecipeViewModel(
        searchRepository,
        stateHandle,
        dispatcher
    )

    @BeforeEach
    fun setUp() {
        searchRepository.setAvailableRecipes(allRecipes)
        searchRecipeViewModel.updateQuery(pastaKeyword)
    }

    @Test
    fun initialQueryLoad() {
        searchRecipeViewModel.findRecipesFor(pastaKeyword)

        assertThat(searchRecipeViewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(
                query = pastaKeyword,
                recipesFound = listOf(pastaWithTomatoes),
                canLoadMore = true
            )
        )
    }

    @Test
    fun initialLoadThenNextPage() {
        searchRecipeViewModel.findRecipesFor(pastaKeyword)
        searchRecipeViewModel.loadMore(1)

        assertThat(searchRecipeViewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(
                query = pastaKeyword,
                recipesFound = listOf(pastaWithTomatoes, pastaWithMushrooms),
                canLoadMore = true
            )
        )
    }

    @Test
    fun loadLastPage() {
        searchRecipeViewModel.loadMore(allRecipes.lastIndex)

        assertThat(searchRecipeViewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(
                query = pastaKeyword,
                recipesFound = listOf(pastaWithMeatBalls),
                canLoadMore = false
            )
        )
    }

    @Test
    fun errorLoadingNextPage() {
        searchRecipeViewModel.findRecipesFor(pastaKeyword)
        searchRepository.setNoRecipesAvailable()

        searchRecipeViewModel.loadMore(1)

        assertThat(searchRecipeViewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(
                query = pastaKeyword,
                recipesFound = listOf(pastaWithTomatoes),
                isSearchingError = true,
                canLoadMore = false
            )
        )
    }

    @Test
    fun loadingNextPageWhileOffline() {
        searchRecipeViewModel.apply {
            findRecipesFor(pastaKeyword)
            loadMore(1)
        }
        searchRepository.setOffline()

        searchRecipeViewModel.loadMore(2)

        assertThat(searchRecipeViewModel.searchScreenState.value).isEqualTo(
            SearchRecipeScreenState(
                query = pastaKeyword,
                recipesFound = listOf(pastaWithTomatoes, pastaWithMushrooms),
                isConnectionError = true,
                canLoadMore = false
            )
        )
    }
}