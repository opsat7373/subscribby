package com.opsat.subscribity.presentation.addsubscription

import androidx.lifecycle.SavedStateHandle
import com.opsat.subscribity.data.seed.SubscriptionSeedData
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.usecase.AddSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.DeleteSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.EditSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.domain.model.AvatarColors
import com.opsat.subscribity.domain.model.SubscriptionIconType
import com.opsat.subscribity.presentation.navigation.SUBSCRIPTION_ID_ARG
import com.opsat.subscribity.testing.FakeIconStorage
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
    private lateinit var iconStorage: FakeIconStorage

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeSubscriptionRepository()
        iconStorage = FakeIconStorage()
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
        iconStorage,
    )

    @Test
    fun `name suggestions are populated as soon as the form opens, before any typing`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(SimpleIconsCatalog.allIcons, viewModel.state.value.filteredNameSuggestions)
    }

    @Test
    fun `initial currency options put already-used currencies first`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        val codes = viewModel.state.value.filteredCurrencyOptions.map { it.code }
        assertEquals(listOf("USD", "EUR", "GBP", "UAH", "JPY"), codes.take(5))
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
        assertEquals(SubscriptionIconType.BRAND, saved.iconType)
        assertEquals("netflix", saved.iconValue)
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
    fun `custom period is resolved from the count and unit fields`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Gym"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("40"))
        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.CustomPeriodCountChanged("45"))
        viewModel.onIntent(AddSubscriptionIntent.CustomPeriodUnitSelected(CustomPeriodUnit.WEEKS))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            BillingPeriod.Custom(45, CustomPeriodUnit.WEEKS),
            repository.addedSubscriptions.single().period,
        )
    }

    @Test
    fun `custom period without a count sets an error and does not persist`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Gym"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("40"))
        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Enter a number greater than 0", viewModel.state.value.customPeriodError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }

    @Test
    fun `custom period error appears immediately when a zero count is typed, without saving`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.CustomPeriodCountChanged("0"))

        assertEquals("Enter a number greater than 0", viewModel.state.value.customPeriodError)
    }

    @Test
    fun `custom period error clears once a valid count is typed`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.PeriodOptionSelected(PeriodOption.CUSTOM))
        viewModel.onIntent(AddSubscriptionIntent.CustomPeriodCountChanged("3"))

        assertEquals(null, viewModel.state.value.customPeriodError)
    }

    @Test
    fun `trial with an empty count blocks save with an error and does not crash`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("15.99"))
        viewModel.onIntent(AddSubscriptionIntent.TrialToggled(true))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Enter a number greater than 0", viewModel.state.value.trialPeriodError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }

    @Test
    fun `trial with an empty price blocks save with an error`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("15.99"))
        viewModel.onIntent(AddSubscriptionIntent.TrialToggled(true))
        viewModel.onIntent(AddSubscriptionIntent.TrialPeriodCountChanged("7"))
        viewModel.onIntent(AddSubscriptionIntent.TrialPriceChanged(""))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Enter a valid price", viewModel.state.value.trialPriceError)
        assertTrue(repository.addedSubscriptions.isEmpty())
    }

    @Test
    fun `a valid trial round-trips into the saved subscription`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("15.99"))
        viewModel.onIntent(AddSubscriptionIntent.CurrencySelected(CurrencyOption("USD", "US Dollar")))
        viewModel.onIntent(AddSubscriptionIntent.TrialToggled(true))
        viewModel.onIntent(AddSubscriptionIntent.TrialPeriodCountChanged("14"))
        viewModel.onIntent(AddSubscriptionIntent.TrialPeriodUnitSelected(CustomPeriodUnit.DAYS))
        viewModel.onIntent(AddSubscriptionIntent.TrialPriceChanged("2.50"))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        val saved = repository.addedSubscriptions.single()
        assertTrue(saved.isTrial)
        assertEquals(BillingPeriod.Custom(14, CustomPeriodUnit.DAYS), saved.trialPeriod)
        assertEquals(BigDecimal("2.50"), saved.trialPrice)
    }

    @Test
    fun `toggling trial off before saving persists no trial data even with stale field text`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.PriceChanged("15.99"))
        viewModel.onIntent(AddSubscriptionIntent.CurrencySelected(CurrencyOption("USD", "US Dollar")))
        viewModel.onIntent(AddSubscriptionIntent.TrialToggled(true))
        viewModel.onIntent(AddSubscriptionIntent.TrialPeriodCountChanged("14"))
        viewModel.onIntent(AddSubscriptionIntent.TrialToggled(false))
        viewModel.onIntent(AddSubscriptionIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        val saved = repository.addedSubscriptions.single()
        assertFalse(saved.isTrial)
        assertEquals(null, saved.trialPeriod)
        assertEquals(null, saved.trialPrice)
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
    fun `editing a custom-period subscription pre-fills the count and unit fields`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val gym = SubscriptionSeedData.subscriptions.first { it.period is BillingPeriod.Custom }
        val viewModel = createViewModel(editingId = gym.id)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(PeriodOption.CUSTOM, viewModel.state.value.periodOption)
        assertEquals("45", viewModel.state.value.customPeriodCountText)
        assertEquals(CustomPeriodUnit.DAYS, viewModel.state.value.customPeriodUnit)
    }

    @Test
    fun `editing the seeded trial subscription pre-fills the trial fields`() = runTest {
        repository.subscriptionsFlow.value = SubscriptionSeedData.subscriptions
        val spotify = SubscriptionSeedData.subscriptions.first { it.isTrial }
        val viewModel = createViewModel(editingId = spotify.id)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isTrial)
        assertEquals(spotify.trialPrice?.toPlainString(), state.trialPriceText)
        // Spotify's seeded trialPeriod is BillingPeriod.Weekly, which the count+unit trial UI can't
        // represent (only BillingPeriod.Custom round-trips) — the pre-fill must surface that as a
        // live error rather than silently leaving Update enabled with an empty count field.
        assertEquals("Enter a number greater than 0", state.trialPeriodError)
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

    @Test
    fun `typing an exact catalog match auto-substitutes the brand icon`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))

        assertEquals(SubscriptionIconType.BRAND, viewModel.state.value.iconType)
        assertEquals("netflix", viewModel.state.value.iconValue)
    }

    @Test
    fun `typing a partial match does not substitute the brand icon`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Net"))

        assertEquals(SubscriptionIconType.LETTER, viewModel.state.value.iconType)
    }

    @Test
    fun `selecting a name suggestion sets the brand icon and closes the dropdown`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        val option = SimpleIconsCatalog.allIcons.first { it.slug == "spotify" }
        viewModel.onIntent(AddSubscriptionIntent.NameSuggestionSelected(option))

        assertEquals(SubscriptionIconType.BRAND, viewModel.state.value.iconType)
        assertEquals("spotify", viewModel.state.value.iconValue)
        assertFalse(viewModel.state.value.isNameSuggestionsExpanded)
    }

    @Test
    fun `once the icon is a brand, further name edits do not change it`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix (family)"))

        assertEquals(SubscriptionIconType.BRAND, viewModel.state.value.iconType)
        assertEquals("netflix", viewModel.state.value.iconValue)
    }

    @Test
    fun `selecting Letter resets to a letter icon with a palette color`() = runTest {
        val viewModel = createViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.NameChanged("Netflix"))
        viewModel.onIntent(AddSubscriptionIntent.LetterIconSelected)

        assertEquals(SubscriptionIconType.LETTER, viewModel.state.value.iconType)
        assertEquals(null, viewModel.state.value.iconValue)
        assertTrue(viewModel.state.value.iconColor in AvatarColors.palette)
    }

    @Test
    fun `deleting a photo subscription deletes its file`() = runTest {
        val photoSubscription = SubscriptionSeedData.subscriptions.first().copy(
            id = 42L,
            iconType = SubscriptionIconType.PHOTO,
            iconValue = "subscription_icons/existing.jpg",
        )
        repository.subscriptionsFlow.value = listOf(photoSubscription)
        val viewModel = createViewModel(editingId = photoSubscription.id)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AddSubscriptionIntent.DeleteClicked)
        viewModel.onIntent(AddSubscriptionIntent.ConfirmDelete)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("subscription_icons/existing.jpg"), iconStorage.deleted)
    }
}
