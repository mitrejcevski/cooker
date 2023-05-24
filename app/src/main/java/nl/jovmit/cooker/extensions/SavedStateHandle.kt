package nl.jovmit.cooker.extensions

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.update(key: String, function: (T) -> T) {
    val currentValue = requireNotNull(get<T>(key))
    val newValue = function(currentValue)
    set(key, newValue)
}