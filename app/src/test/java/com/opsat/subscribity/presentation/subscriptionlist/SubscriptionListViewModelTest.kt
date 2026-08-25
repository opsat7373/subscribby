package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.data.seed.SubscriptionSeedData
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.testing.FakeSubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionListViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeSubscriptionRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeSubscriptionRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SubscriptionListViewModel(ObserveSubscriptionsUseCase(repository))

    @Test
    fun `initial state maps repository subscriptions`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val viewModel = createViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(SubscriptionSeedData.subscriptions.map { it.toUiModel() }, state.subscriptions)
    }

    @Test
    fun `state auto-updates when the repository flow emits a new list`() = runTest {
        repository.subscriptionsFlow.value = listOf(SubscriptionSeedData.subscriptions.first())
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.state.value.subscriptions.size)

        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.subscriptions.size == SubscriptionSeedData.subscriptions.size)
        assertEquals(SubscriptionSeedData.subscriptions.map { it.toUiModel() }, viewModel.state.value.subscriptions)
    }
}
