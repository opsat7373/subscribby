package com.opsat.subscribity.presentation.addsubscription

import androidx.lifecycle.SavedStateHandle
import com.opsat.subscribity.data.seed.SubscriptionSeedData
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.usecase.AddSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.DeleteSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.EditSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.presentation.navigation.SUBSCRIPTION_ID_ARG
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
import org.junit.Assert.assertFalse
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

    private fun createViewModel(editingId: Long = 0L) = AddSubscriptionViewModel(
        SavedStateHandle(mapOf(SUBSCRIPTION_ID_ARG to editingId)),
        AddSubscriptionUseCase(repository),
        EditSubscriptionUseCase(repository),
        DeleteSubscriptionUseCase(repository),
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

    @Test
    fun `editing an existing subscription pre-fills the form`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(AddSubscriptionMode.Edit(netflix.id, netflix.name), state.mode)
        assertEquals(netflix.name, state.name)
        assertEquals(netflix.price.toPlainString(), state.priceText)
        assertEquals(netflix.currency.code, state.selectedCurrency?.code)
        assertEquals(PeriodOption.MONTHLY, state.periodOption)
        assertEquals(netflix.nextPaymentDate, state.nextPaymentDate)
    }

    @Test
    fun `editing a custom-period subscription pre-fills the days field`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val gym = SubscriptionSeedData.subscriptions.first { it.period is BillingPeriod.Custom }
        val viewModel = createViewModel(editingId = gym.id)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(PeriodOption.CUSTOM, viewModel.state.value.periodOption)
        assertEquals("45", viewModel.state.value.customPeriodDaysText)
    }

    @Test
    fun `the title stays frozen after the name field is edited`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Something Else"))

        val mode = viewModel.state.value.mode as AddSubscriptionMode.Edit
        assertEquals(netflix.name, mode.originalName)
        assertEquals("Something Else", viewModel.state.value.name)
    }

    @Test
    fun `save in edit mode opens the update confirmation without persisting`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("19.99"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isUpdateConfirmationVisible)
        assertEquals(netflix, repository.subscriptionsFlow.value.first { it.id == netflix.id })
        assertTrue(effects.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun `confirming update persists changes and navigates back`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("19.99"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        viewModel.onIntent(AddSubscriptionIntent.ConfirmUpdate)
        dispatcher.scheduler.advanceUntilIdle()

        val updated = repository.subscriptionsFlow.value.first { it.id == netflix.id }
        assertEquals(BigDecimal("19.99"), updated.price)
        assertFalse(viewModel.state.value.isUpdateConfirmationVisible)
        assertEquals(listOf(AddSubscriptionEffect.NavigateBack), effects)

        collectJob.cancel()
    }

    @Test
    fun `dismissing the update confirmation persists nothing`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("19.99"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        viewModel.onIntent(AddSubscriptionIntent.DismissUpdateConfirmation)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.isUpdateConfirmationVisible)
        assertEquals(netflix, repository.subscriptionsFlow.value.first { it.id == netflix.id })
    }

    @Test
    fun `delete requires confirmation before removing the subscription`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.DeleteClicked)
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isDeleteConfirmationVisible)
        assertTrue(repository.subscriptionsFlow.value.any { it.id == netflix.id })
        assertTrue(effects.isEmpty())

        collectJob.cancel()
    }

    @Test
    fun `confirming delete removes the subscription and navigates back`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        val effects = mutableListOf<AddSubscriptionEffect>()
        val collectJob = launch { viewModel.effects.toList(effects) }

        viewModel.onIntent(AddSubscriptionIntent.DeleteClicked)
        viewModel.onIntent(AddSubscriptionIntent.ConfirmDelete)
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(repository.subscriptionsFlow.value.none { it.id == netflix.id })
        assertFalse(viewModel.state.value.isDeleteConfirmationVisible)
        assertEquals(listOf(AddSubscriptionEffect.NavigateBack), effects)

        collectJob.cancel()
    }

    @Test
    fun `dismissing the delete confirmation keeps the subscription`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val netflix = SubscriptionSeedData.subscriptions.first()
        val viewModel = createViewModel(editingId = netflix.id)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.DeleteClicked)
        viewModel.onIntent(AddSubscriptionIntent.DismissDeleteConfirmation)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.isDeleteConfirmationVisible)
        assertTrue(repository.subscriptionsFlow.value.any { it.id == netflix.id })
    }
}
