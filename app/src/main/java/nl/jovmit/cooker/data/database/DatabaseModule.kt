package nl.jovmit.cooker.data.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "recipes"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RecipesDatabase {
        return Room
            .databaseBuilder(context, RecipesDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipesDao(database: RecipesDatabase): RecipesDao {
        return database.recipeDao()
    }
}