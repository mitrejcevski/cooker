package nl.jovmit.cooker.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.jovmit.cooker.R
import nl.jovmit.cooker.model.settings.UiMode
import nl.jovmit.cooker.ui.settings.state.SettingsScreenState
import nl.jovmit.cooker.ui.theme.CookerTheme

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val state by settingsViewModel.settingsScreenState.collectAsStateWithLifecycle()
    SettingsScreenContent(
        screenState = state,
        onUiModeSelected = { newUiMode -> settingsViewModel.updateUiMode(newUiMode) },
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    screenState: SettingsScreenState,
    onUiModeSelected: (newUiMode: UiMode) -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_navigate_up)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Ui Mode",
                style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UiModeSelector(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .weight(1f)
                        .clickable { onUiModeSelected(UiMode.Light) },
                    title = stringResource(id = R.string.ui_mode_light),
                    isSelected = screenState.uiMode == UiMode.Light
                )
                UiModeSelector(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .weight(1f)
                        .clickable { onUiModeSelected(UiMode.Dark) },
                    title = stringResource(id = R.string.ui_mode_dark),
                    isSelected = screenState.uiMode == UiMode.Dark
                )
                UiModeSelector(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .weight(1f)
                        .clickable { onUiModeSelected(UiMode.System) },
                    title = stringResource(id = R.string.ui_mode_system),
                    isSelected = screenState.uiMode == UiMode.System
                )
            }
        }
    }
}

@Composable
private fun UiModeSelector(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean = false,
) {
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    Column(
        modifier = modifier.border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(12.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = title
        )
        RadioButton(
            modifier = Modifier.padding(12.dp),
            selected = isSelected,
            onClick = null
        )
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSettings() {
    CookerTheme {
        SettingsScreenContent(
            modifier = Modifier.fillMaxSize(),
            screenState = SettingsScreenState(),
            onUiModeSelected = {},
            onNavigateUp = {}
        )
    }
}