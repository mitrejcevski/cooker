package nl.jovmit.cooker.data.network

sealed class RemoteRecipeResult {
    data class RecipeValue(val value: SearchResponse.RecipeResponse) : RemoteRecipeResult()
    object BackendError : RemoteRecipeResult()
    object ConnectionError : RemoteRecipeResult()
}