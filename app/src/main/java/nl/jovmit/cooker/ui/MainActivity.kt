package nl.jovmit.cooker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.jovmit.cooker.ui.details.RecipeDetailsScreen
import nl.jovmit.cooker.model.settings.UiMode
import nl.jovmit.cooker.ui.search.SearchRecipeScreen
import nl.jovmit.cooker.ui.settings.SettingsScreen
import nl.jovmit.cooker.ui.settings.SettingsViewModel
import nl.jovmit.cooker.ui.theme.CookerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiMode by settingsViewModel.settingsScreenState.collectAsStateWithLifecycle()
            val isDarkMode = when(uiMode.uiMode) {
                is UiMode.Dark -> true
                is UiMode.Light -> false
                is UiMode.System -> isSystemInDarkTheme()
            }
            CookerTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}