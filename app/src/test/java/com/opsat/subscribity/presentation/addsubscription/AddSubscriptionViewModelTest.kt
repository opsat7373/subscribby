package com.opsat.subscribity.presentation.addsubscription

import com.opsat.subscribity.data.seed.SubscriptionSeedData
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.usecase.AddSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.testing.FakeSubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class AddSubscriptionViewModelTest {

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

    private fun createViewModel() = AddSubscriptionViewModel(
        AddSubscriptionUseCase(repository),
        ObserveSubscriptionsUseCase(repository),
    )

    @Test
    fun `initial currency options put already-used currencies first`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        val codes = viewModel.state.value.filteredCurrencyOptions.map { it.code }
        assertEquals("USD", codes[0])
        assertEquals("UAH", codes[1])
    }

    @Test
    fun `save with blank name sets an error and does not persist`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("9.99"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Name is required", viewModel.state.value.nameError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }

    @Test
    fun `save with an invalid price sets an error and does not persist`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Enter a valid price", viewModel.state.value.priceError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }

    @Test
    fun `save with valid data persists the subscription and navigates back`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("15.99"))
        viewModel.onIntent(AddSubscriptionIntent.CurrencySelected(CurrencyOption("USD", "US Dollar")))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, repository.addedSubscriptions.size)
        val saved = repository.addedSubscriptions.single()
        assertEquals("Netflix", saved.name)
        assertEquals("Netflix", saved.icon)
        assertEquals(BigDecimal("15.99"), saved.price)
        assertEquals("USD", saved.currency.code)
        assertEquals(BillingPeriod.Monthly, saved.period)
        assertEquals(listOf(AddSubscriptionEffect.NavigateBack), effects)

        collectJob.cancel()
    }

    @Test
    fun `cancel navigates back without persisting`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.Cancel)
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(repository.addedSubscriptions.isEmpty())
        assertEquals(listOf(AddSubscriptionEffect.NavigateBack), effects)

        collectJob.cancel()
    }

    @Test
    fun `custom period is resolved from the days field`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Gym"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("40"))
        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.CustomPeriodDaysChanged("45"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(BillingPeriod.Custom(45), repository.addedSubscriptions.single().period)
    }

    @Test
    fun `custom period without a day count sets an error and does not persist`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Gym"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("40"))
        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Enter a number of days", viewModel.state.value.customPeriodError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }
}
