package nl.jovmit.cooker.ui.settings

import com.google.common.truth.Truth.assertThat
import nl.jovmit.cooker.data.InMemorySharedPreferences
import nl.jovmit.cooker.domain.settings.AppSettingsRepository
import nl.jovmit.cooker.model.settings.UiMode
import nl.jovmit.cooker.ui.settings.state.SettingsScreenState
import org.junit.jupiter.api.Test

class ChangeUiModeTest {

    private val preferences = InMemorySharedPreferences()

    @Test
    fun defaultUiMode() {
        val appSettings = AppSettingsRepository(preferences)
        val viewModel = SettingsViewModel(appSettings)

        assertThat(viewModel.settingsScreenState.value)
            .isEqualTo(SettingsScreenState(uiMode = UiMode.System))
    }

    @Test
    fun previouslyStoredUiMode() {
        val uiMode = UiMode.Dark
        val appSettings = AppSettingsRepository(preferences).apply {
            setUiMode(uiMode)
        }
        val viewModel = SettingsViewModel(appSettings)

        assertThat(viewModel.settingsScreenState.value)
            .isEqualTo(SettingsScreenState(uiMode = uiMode))
    }

    @Test
    fun updateUiMode() {
        val newUiMode = UiMode.Light
        val appSettings = AppSettingsRepository(preferences)
        val viewModel = SettingsViewModel(appSettings)

        viewModel.updateUiMode(newUiMode)

        assertThat(appSettings.getUiMode()).isEqualTo(newUiMode)
        assertThat(viewModel.settingsScreenState.value)
            .isEqualTo(SettingsScreenState(uiMode = newUiMode))
    }
}