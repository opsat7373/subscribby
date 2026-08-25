package com.opsat.subscribity.presentation.subscriptionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SubscriptionListViewModel @Inject constructor(
    observeSubscriptions: ObserveSubscriptionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionListState())
    val state: StateFlow<SubscriptionListState> = _state.asStateFlow()

    init {
        observeSubscriptions()
            .onEach { subscriptions ->
                _state.update {
                    it.copy(isLoading = false, subscriptions = subscriptions.map { s -> s.toUiModel() })
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SubscriptionListIntent) {
        when (intent) {
            is SubscriptionListIntent.SelectSubscription -> {
                // No detail/edit screen or navigation exists yet; wired for future use.
            }
        }
    }
}
