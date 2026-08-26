package com.opsat.subscribity.presentation.subscriptionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.model.monthlySpendingByCurrency
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionListViewModel @Inject constructor(
    observeSubscriptions: ObserveSubscriptionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionListState())
    val state: StateFlow<SubscriptionListState> = _state.asStateFlow()

    private val _effects = Channel<SubscriptionListEffect>(Channel.BUFFERED)
    val effects: Flow<SubscriptionListEffect> = _effects.receiveAsFlow()

    init {
        observeSubscriptions()
            .onEach { subscriptions ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        subscriptions = subscriptions.map { s -> s.toUiModel() },
                        monthlySpending = subscriptions.monthlySpendingByCurrency().map { it.toUiModel() },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SubscriptionListIntent) {
        when (intent) {
            is SubscriptionListIntent.SelectSubscription -> viewModelScope.launch {
                _effects.send(SubscriptionListEffect.NavigateToEditSubscription(intent.id))
            }
        }
    }
}
