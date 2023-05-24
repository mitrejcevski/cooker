package nl.jovmit.cooker.domain

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository
import nl.jovmit.cooker.extensions.test
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.RecipeItemBuilder.Companion.aRecipeItem
import nl.jovmit.cooker.model.search.RecentRecipesResult
import nl.jovmit.cooker.model.search.SearchResult
import org.junit.jupiter.api.Test

abstract class SearchRecipeRepositoryContractTest {

    private val cake = aRecipeItem().withTitle("Cake").build()
    private val chickenSoup = aRecipeItem().withTitle("Chicken Soup").build()
    private val fish = aRecipeItem().withTitle("Fish").build()
    private val pork = aRecipeItem().withTitle("Pork").build()
    private val beef = aRecipeItem().withTitle("Beef").build()
    private val friedChicken = aRecipeItem().withTitle("Fried Chicken").build()

    @Test
    fun noRecentRecipes() = runTest {
        val repository = searchRecipeRepositoryWith(recentRecipes = emptyList())

        repository.loadRecentRecipes().test { result ->
            assertThat(result).isEqualTo(RecentRecipesResult.Recent(emptyList()))
        }
    }

    @Test
    fun recentRecipesAvailable() = runTest {
        val repository = searchRecipeRepositoryWith(recentRecipes = listOf(chickenSoup))

        repository.loadRecentRecipes().test { result ->
            assertThat(result).isEqualTo(RecentRecipesResult.Recent(listOf(chickenSoup)))
        }
    }

    @Test
    fun errorLoadingRecent() = runTest {
        val repository = searchRecipeRepositoryWithUnavailableRecipes()

        repository.loadRecentRecipes().test { result ->
            assertThat(result).isEqualTo(RecentRecipesResult.LoadingError)
        }
    }

    @Test
    fun noSearchResults() = runTest {
        val repository = searchRecipeRepositoryWith(availableRecipes = listOf(cake, fish, pork))

        val result = repository.searchRecipes(query = "chicken", offset = 0)

        assertThat(result).isEqualTo(SearchResult.Matches(emptyList(), false))
    }

    @Test
    fun matchingSearchResults() = runTest {
        val repository = searchRecipeRepositoryWith(
            availableRecipes = listOf(cake, chickenSoup, fish, pork, beef, friedChicken)
        )

        val result = repository.searchRecipes(query = "chicken", offset = 0)

        assertThat(result).isEqualTo(SearchResult.Matches(listOf(chickenSoup, friedChicken), false))
    }

    @Test
    fun searchingRecipeBackendError() = runTest {
        val repository = searchRecipeRepositoryWithUnavailableRecipes()

        val result = repository.searchRecipes(query = ":irrelevant:", offset = 0)

        assertThat(result).isEqualTo(SearchResult.BackendError)
    }

    @Test
    fun searchingRecipeOfflineError() = runTest {
        val repository = offlineSearchRecipeRepository()

        val result = repository.searchRecipes(query = ":irrelevant:", offset = 0)

        assertThat(result).isEqualTo(SearchResult.Offline)
    }

    abstract fun searchRecipeRepositoryWith(
        recentRecipes: List<RecipeItem> = emptyList(),
        availableRecipes: List<RecipeItem> = emptyList()
    ): SearchRecipeRepository

    abstract fun searchRecipeRepositoryWithUnavailableRecipes(): SearchRecipeRepository

    abstract fun offlineSearchRecipeRepository(): SearchRecipeRepository
}