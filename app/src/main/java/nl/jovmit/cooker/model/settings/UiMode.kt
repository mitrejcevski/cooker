package nl.jovmit.cooker.model.settings

sealed class UiMode {
    object System : UiMode()
    object Light : UiMode()
    object Dark : UiMode()
}