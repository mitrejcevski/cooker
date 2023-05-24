package nl.jovmit.cooker.domain.recipe

import nl.jovmit.cooker.data.database.IngredientEntity
import nl.jovmit.cooker.data.database.InstructionEntity
import nl.jovmit.cooker.data.database.RecipeEntity
import nl.jovmit.cooker.data.database.RecipeWithInstructionsAndIngredients
import nl.jovmit.cooker.data.network.SearchResponse
import nl.jovmit.cooker.data.network.SearchResponse.RecipeResponse.Ingredient
import nl.jovmit.cooker.model.RecipeItem

class RecipeItemMapper {

    fun recipeResponseToRecipeItem(response: SearchResponse.RecipeResponse): RecipeItem {
        return RecipeItem(
            id = response.id,
            title = response.title,
            readyInMinutes = response.readyInMinutes,
            image = response.image,
            summary = response.summary,
            isVegetarian = response.vegetarian,
            isVegan = response.vegan,
            healthScore = response.healthScore,
            servings = response.servings,
            sourceUrl = response.sourceUrl
        )
    }

    fun recipeEntityToRecipeItem(entity: RecipeEntity): RecipeItem {
        return RecipeItem(
            id = entity.recipeId,
            title = entity.title,
            readyInMinutes = entity.readyInMinutes,
            image = entity.image,
            summary = entity.summary,
            isVegetarian = entity.isVegetarian,
            isVegan = entity.isVegan,
            healthScore = entity.healthScore,
            servings = entity.servings,
            sourceUrl = entity.sourceUrl,
            isFavorite = entity.isFavorite
        )
    }

    fun recipeResponseToRecipeEntity(response: SearchResponse.RecipeResponse): RecipeEntity {
        return RecipeEntity(
            recipeId = response.id,
            title = response.title,
            image = response.image,
            summary = response.summary,
            readyInMinutes = response.readyInMinutes,
            isVegetarian = response.vegetarian,
            isVegan = response.vegan,
            healthScore = response.healthScore,
            servings = response.servings,
            sourceUrl = response.sourceUrl,
            isFavorite = false
        )
    }

    fun instructionStepToInstructionEntity(
        recipeId: Long,
        step: SearchResponse.RecipeResponse.Instruction.Step
    ): InstructionEntity {
        return InstructionEntity(
            recipeId = recipeId,
            number = step.number,
            step = step.step
        )
    }

    fun ingredientResponseToIngredientEntity(
        recipeId: Long,
        ingredient: Ingredient
    ): IngredientEntity {
        return IngredientEntity(
            id = ingredient.id,
            recipeId = recipeId,
            name = ingredient.name,
            image = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}",
            amount = ingredient.amount,
            unit = ingredient.unit
        )
    }

    fun fullRecipeEntityToRecipeItem(
        fullRecipe: RecipeWithInstructionsAndIngredients
    ): RecipeItem {
        return RecipeItem(
            id = fullRecipe.recipe.recipeId,
            title = fullRecipe.recipe.title,
            readyInMinutes = fullRecipe.recipe.readyInMinutes,
            image = fullRecipe.recipe.image,
            summary = fullRecipe.recipe.summary,
            isVegetarian = fullRecipe.recipe.isVegetarian,
            isVegan = fullRecipe.recipe.isVegan,
            healthScore = fullRecipe.recipe.healthScore,
            servings = fullRecipe.recipe.servings,
            sourceUrl = fullRecipe.recipe.sourceUrl,
            isFavorite = fullRecipe.recipe.isFavorite,
            instructions = fullRecipe.instructions.map { it.toInstructionItem() },
            ingredients = fullRecipe.ingredients.map { it.toIngredientItem() }
        )
    }

    private fun InstructionEntity.toInstructionItem(): RecipeItem.Instruction {
        return RecipeItem.Instruction(number, step)
    }

    private fun IngredientEntity.toIngredientItem(): RecipeItem.Ingredient {
        return RecipeItem.Ingredient(name, image, amount, unit)
    }
}