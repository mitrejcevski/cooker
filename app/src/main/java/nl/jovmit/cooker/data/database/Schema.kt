package nl.jovmit.cooker.data.database

object Schema {
    const val VERSION = 1

    object Recipe {
        const val TABLE_NAME = "recipe"
        const val ID = "id"
        const val TITLE = "title"
        const val IMAGE = "image"
        const val SUMMARY = "summary"
        const val READY_IN_MINUTES = "readyInMinutes"
        const val IS_VEGETARIAN = "isVegetarian"
        const val IS_VEGAN = "isVegan"
        const val HEALTH_SCORE = "healthScore"
        const val SERVINGS = "servings"
        const val SOURCE_URL = "sourceUrl"
        const val IS_FAVORITE = "isFavorite"
    }

    object Instruction {
        const val TABLE_NAME = "instruction"
        const val RECIPE_ID = "recipeId"
        const val NUMBER = "number"
        const val STEP = "step"
    }

    object Ingredient {
        const val TABLE_NAME = "ingredient"
        const val ID = "id"
        const val RECIPE_ID = "recipe_id"
        const val NAME = "name"
        const val IMAGE = "image"
        const val AMOUNT = "amount"
        const val UNIT = "unit"
    }
}