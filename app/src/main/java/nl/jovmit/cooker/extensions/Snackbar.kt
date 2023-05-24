package nl.jovmit.cooker.extensions

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SnackbarHostState.ShowSnackbar(
    message: String,
    onDismissed: () -> Unit
) {
    LaunchedEffect(message) {
        val result = showSnackbar(message = message)
        if (result == SnackbarResult.Dismissed) {
            onDismissed()
        }
    }
}