package nl.jovmit.cooker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Query("SELECT * FROM ${Schema.Recipe.TABLE_NAME}")
    fun getRecentRecipes(): Flow<List<RecipeEntity>?>

    @Query("SELECT * FROM ${Schema.Recipe.TABLE_NAME} WHERE ${Schema.Recipe.ID} = :recipeId")
    fun getRecipeFor(recipeId: Long): Flow<RecipeWithInstructionsAndIngredients?>

    @Query("UPDATE ${Schema.Recipe.TABLE_NAME} SET isFavorite = ((isFavorite | 1) - (isFavorite & 1)) WHERE id = :recipeId")
    suspend fun toggleFavorite(recipeId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructions(instructions: List<InstructionEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)
}