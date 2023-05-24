package nl.jovmit.cooker.data.database

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithInstructionsAndIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = Schema.Recipe.ID,
        entityColumn = Schema.Instruction.RECIPE_ID
    )
    val instructions: List<InstructionEntity>,
    @Relation(
        parentColumn = Schema.Recipe.ID,
        entityColumn = Schema.Ingredient.RECIPE_ID
    )
    val ingredients: List<IngredientEntity>
)