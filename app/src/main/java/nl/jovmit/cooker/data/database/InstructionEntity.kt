package nl.jovmit.cooker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = Schema.Instruction.TABLE_NAME,
    primaryKeys = [Schema.Instruction.RECIPE_ID, Schema.Instruction.NUMBER],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = arrayOf(Schema.Recipe.ID),
            childColumns = arrayOf(Schema.Instruction.RECIPE_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InstructionEntity(
    @ColumnInfo(name = Schema.Instruction.RECIPE_ID) val recipeId: Long,
    @ColumnInfo(name = Schema.Instruction.NUMBER) val number: Int,
    @ColumnInfo(name = Schema.Instruction.STEP) val step: String
)