package nl.jovmit.cooker.model.details

import nl.jovmit.cooker.model.RecipeItem

sealed class RecipeResult {

    object BackendError : RecipeResult()

    object NotFoundError : RecipeResult()

    object Offline: RecipeResult()

    data class Recipe(
        val value: RecipeItem,
        val isBackendError: Boolean = false,
        val isOffline: Boolean = false
    ) : RecipeResult()
}
