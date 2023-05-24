package nl.jovmit.cooker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecipeEntity::class,
        InstructionEntity::class,
        IngredientEntity::class
    ],
    version = Schema.VERSION, exportSchema = false
)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipesDao
}