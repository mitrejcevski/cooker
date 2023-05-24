package nl.jovmit.cooker.domain

import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository
import nl.jovmit.cooker.model.RecipeItem

class InMemorySearchRecipeRepositoryTest : SearchRecipeRepositoryContractTest() {

    override fun searchRecipeRepositoryWith(
        recentRecipes: List<RecipeItem>,
        availableRecipes: List<RecipeItem>
    ): SearchRecipeRepository {
        return InMemorySearchRecipeRepository().apply {
            setRecentRecipes(recentRecipes)
            setAvailableRecipes(availableRecipes)
        }
    }

    override fun searchRecipeRepositoryWithUnavailableRecipes(): SearchRecipeRepository {
        return InMemorySearchRecipeRepository().apply {
            setRecentRecipesUnavailable()
            setNoRecipesAvailable()
        }
    }

    override fun offlineSearchRecipeRepository(): SearchRecipeRepository {
        return InMemorySearchRecipeRepository().apply {
            setOffline()
        }
    }
}