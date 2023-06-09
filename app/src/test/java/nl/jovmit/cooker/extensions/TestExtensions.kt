package nl.jovmit.cooker.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun <T> CoroutineScope.observeFlow(
    stateFlow: StateFlow<T>,
    dropInitialValue: Boolean = true,
    dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
    block: () -> Unit
): List<T> {
    val result = mutableListOf<T>()
    val collectJob = launch(dispatcher) {
        stateFlow.toCollection(result)
    }
    block()
    collectJob.cancel()
    return if (dropInitialValue) result.drop(1) else result
}

suspend fun <T> Flow<T>.test(action: (T) -> Unit) {
    this.collectLatest { item -> action(item) }
}