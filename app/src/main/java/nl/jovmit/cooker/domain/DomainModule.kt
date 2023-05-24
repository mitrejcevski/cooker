package nl.jovmit.cooker.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nl.jovmit.cooker.domain.recipe.OfflineFirstRecipeDetailsRepository
import nl.jovmit.cooker.domain.recipe.RecipeDetailsRepository
import nl.jovmit.cooker.domain.recipe.RecentFirstSearchRecipeRepository
import nl.jovmit.cooker.domain.recipe.SearchRecipeRepository

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    fun provideSearchRecipeRepository(
        recentFirstSearchRecipeRepository: RecentFirstSearchRecipeRepository
    ): SearchRecipeRepository

    @Binds
    fun provideRecipeDetailsRepository(
        offlineFirstRecipeDetailsRepository: OfflineFirstRecipeDetailsRepository
    ): RecipeDetailsRepository
}