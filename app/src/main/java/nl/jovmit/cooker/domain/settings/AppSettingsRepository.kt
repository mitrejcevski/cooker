package nl.jovmit.cooker.domain.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import nl.jovmit.cooker.model.settings.UiMode
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getUiMode(): UiMode {
        val uiModeValue = sharedPreferences.getString(UI_MODE_KEY, SYSTEM) ?: SYSTEM
        return uiModeValue.toUiMode()
    }

    fun setUiMode(uiMode: UiMode) {
        val uiModeValue = uiMode.toPreferenceValue()
        sharedPreferences.edit {
            putString(UI_MODE_KEY, uiModeValue)
        }
    }

    private fun String.toUiMode(): UiMode = when (this) {
        LIGHT -> UiMode.Light
        DARK -> UiMode.Dark
        else -> UiMode.System
    }

    private fun UiMode.toPreferenceValue() = when (this) {
        is UiMode.Light -> LIGHT
        is UiMode.Dark -> DARK
        is UiMode.System -> SYSTEM
    }

    companion object {
        private const val UI_MODE_KEY = "ui_mode"
        private const val LIGHT = "light"
        private const val DARK = "dark"
        private const val SYSTEM = "system"
    }
}