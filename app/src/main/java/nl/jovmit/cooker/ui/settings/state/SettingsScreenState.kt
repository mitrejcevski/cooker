package nl.jovmit.cooker.ui.settings.state

import nl.jovmit.cooker.model.settings.UiMode

data class SettingsScreenState(
    val uiMode: UiMode = UiMode.System
)
