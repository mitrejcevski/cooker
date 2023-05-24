package nl.jovmit.cooker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Schema.Recipe.TABLE_NAME)
data class RecipeEntity(
    @PrimaryKey @ColumnInfo(name = Schema.Recipe.ID) val recipeId: Long,
    @ColumnInfo(name = Schema.Recipe.TITLE) val title: String,
    @ColumnInfo(name = Schema.Recipe.IMAGE) val image: String,
    @ColumnInfo(name = Schema.Recipe.SUMMARY) val summary: String,
    @ColumnInfo(name = Schema.Recipe.READY_IN_MINUTES) val readyInMinutes: Int,
    @ColumnInfo(name = Schema.Recipe.IS_VEGETARIAN) val isVegetarian: Boolean,
    @ColumnInfo(name = Schema.Recipe.IS_VEGAN) val isVegan: Boolean,
    @ColumnInfo(name = Schema.Recipe.HEALTH_SCORE) val healthScore: Int,
    @ColumnInfo(name = Schema.Recipe.SERVINGS) val servings: Int,
    @ColumnInfo(name = Schema.Recipe.SOURCE_URL) val sourceUrl: String,
    @ColumnInfo(name = Schema.Recipe.IS_FAVORITE, defaultValue = "0") val isFavorite: Boolean
)