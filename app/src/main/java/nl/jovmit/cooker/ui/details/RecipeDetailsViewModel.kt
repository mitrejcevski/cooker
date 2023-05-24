package nl.jovmit.cooker.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.jovmit.cooker.domain.recipe.RecipeDetailsRepository
import nl.jovmit.cooker.extensions.update
import nl.jovmit.cooker.model.RecipeItem
import nl.jovmit.cooker.model.details.RecipeResult
import nl.jovmit.cooker.ui.details.state.RecipeDetailsScreenState
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeRepository: RecipeDetailsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val backgroundDispatcher: CoroutineDispatcher
) : ViewModel() {

    val recipeDetailsScreenState: StateFlow<RecipeDetailsScreenState> =
        savedStateHandle.getStateFlow(RECIPE_DETAILS_KEY, RecipeDetailsScreenState())

    fun loadRecipeDetails(recipeId: Long) {
        viewModelScope.launch {
            setLoading()
            withContext(backgroundDispatcher) {
                recipeRepository.fetchRecipeDetails(recipeId).onEach { newValue ->
                    setStateFor(newValue)
                }
            }.stateIn(viewModelScope)
        }
    }

    fun toggleFavorite(recipeId: Long) {
        viewModelScope.launch {
            withContext(backgroundDispatcher) {
                recipeRepository.toggleFavorite(recipeId)
            }
        }
    }

    fun dismissBackendError() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isBackendError = false)
        }
    }

    fun dismissConnectionError() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isConnectionError = false)
        }
    }

    private fun setStateFor(recipeResult: RecipeResult) {
        when (recipeResult) {
            is RecipeResult.Recipe -> setRecipeLoaded(
                recipe = recipeResult.value,
                isBackendError = recipeResult.isBackendError,
                isOffline = recipeResult.isOffline,
            )

            is RecipeResult.NotFoundError -> setRecipeNotFound()
            is RecipeResult.BackendError -> setBackendError()
            is RecipeResult.Offline -> setOfflineError()
        }
    }

    private fun setLoading() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isLoading = true)
        }
    }

    private fun setRecipeLoaded(
        recipe: RecipeItem,
        isBackendError: Boolean,
        isOffline: Boolean
    ) {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(
                isLoading = false,
                recipeDetails = recipe,
                isBackendError = isBackendError,
                isConnectionError = isOffline
            )
        }
    }

    private fun setRecipeNotFound() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isLoading = false, isRecipeNotFoundError = true)
        }
    }

    private fun setBackendError() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isLoading = false, isBackendError = true)
        }
    }

    private fun setOfflineError() {
        savedStateHandle.update<RecipeDetailsScreenState>(RECIPE_DETAILS_KEY) {
            it.copy(isLoading = false, isConnectionError = true)
        }
    }

    companion object {
        private const val RECIPE_DETAILS_KEY = "recipeDetailsScreenKey"
    }
}