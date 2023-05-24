package nl.jovmit.cooker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = Schema.Ingredient.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = arrayOf(Schema.Recipe.ID),
            childColumns = arrayOf(Schema.Ingredient.RECIPE_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    @PrimaryKey @ColumnInfo(name = Schema.Ingredient.ID) val id: Long,
    @ColumnInfo(name = Schema.Ingredient.RECIPE_ID) val recipeId: Long,
    @ColumnInfo(name = Schema.Ingredient.NAME) val name: String,
    @ColumnInfo(name = Schema.Ingredient.IMAGE) val image: String,
    @ColumnInfo(name = Schema.Ingredient.AMOUNT) val amount: Double,
    @ColumnInfo(name = Schema.Ingredient.UNIT) val unit: String
)
