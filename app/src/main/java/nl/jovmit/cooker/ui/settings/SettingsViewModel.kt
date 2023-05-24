package nl.jovmit.cooker.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.jovmit.cooker.domain.settings.AppSettingsRepository
import nl.jovmit.cooker.model.settings.UiMode
import nl.jovmit.cooker.ui.settings.state.SettingsScreenState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettingsRepository
) : ViewModel() {

    private val _settingsScreenState =
        MutableStateFlow(SettingsScreenState(uiMode = appSettings.getUiMode()))
    val settingsScreenState: StateFlow<SettingsScreenState> = _settingsScreenState.asStateFlow()

    fun updateUiMode(newMode: UiMode) {
        appSettings.setUiMode(newMode)
        _settingsScreenState.update { it.copy(uiMode = newMode) }
    }
}