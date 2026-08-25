package com.opsat.subscribity.presentation.addsubscription

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.presentation.theme.SubscribityTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AddSubscriptionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun expandedCurrencyMenuShowsOptions() {
        val state = AddSubscriptionState(
            selectedCurrency = CurrencyOption("USD", "US Dollar"),
            currencyQuery = "USD",
            allCurrencyOptions = listOf(
                CurrencyOption("USD", "US Dollar"),
                CurrencyOption("UAH", "Ukrainian Hryvnia"),
                CurrencyOption("EUR", "Euro"),
            ),
            filteredCurrencyOptions = listOf(
                CurrencyOption("USD", "US Dollar"),
                CurrencyOption("UAH", "Ukrainian Hryvnia"),
                CurrencyOption("EUR", "Euro"),
            ),
            isCurrencyMenuExpanded = true,
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("UAH — Ukrainian Hryvnia").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR — Euro").assertIsDisplayed()
    }

    @Test
    fun customPeriodShowsCountAndUnitFields() {
        val state = AddSubscriptionState(
            periodOption = PeriodOption.CUSTOM,
            customPeriodUnit = CustomPeriodUnit.WEEKS,
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Every").assertIsDisplayed()
        composeTestRule.onNodeWithText("Weeks").assertIsDisplayed()
    }

    @Test
    fun saveIsDisabledWhenCustomPeriodCountIsInvalid() {
        val state = AddSubscriptionState(
            periodOption = PeriodOption.CUSTOM,
            customPeriodError = "Enter a number greater than 0",
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Enter a number greater than 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun saveIsEnabledWhenCustomPeriodCountIsValid() {
        val state = AddSubscriptionState(
            periodOption = PeriodOption.CUSTOM,
            customPeriodCountText = "3",
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun trialFieldsAreHiddenWhenTheSwitchIsOff() {
        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = AddSubscriptionState(isTrial = false), onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Free trial").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trial price").assertDoesNotExist()
    }

    @Test
    fun trialFieldsAreShownWhenTheSwitchIsOn() {
        val state = AddSubscriptionState(isTrial = true, trialPeriodUnit = CustomPeriodUnit.WEEKS)

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Trial price").assertIsDisplayed()
    }

    @Test
    fun firstChargeLabelReflectsTheStartDatePlusTrialLength() {
        val startDate = LocalDate.of(2026, 9, 1)
        val state = AddSubscriptionState(
            isTrial = true,
            nextPaymentDate = startDate,
            trialPeriodCountText = "14",
            trialPeriodUnit = CustomPeriodUnit.DAYS,
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        val expectedDate = startDate.plusDays(14).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        composeTestRule.onNodeWithText("First charge on $expectedDate").assertIsDisplayed()
    }

    @Test
    fun saveIsDisabledWhenTrialCountIsInvalid() {
        val state = AddSubscriptionState(
            isTrial = true,
            trialPeriodError = "Enter a number greater than 0",
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun formFieldErrorsAreDisplayed() {
        val state = AddSubscriptionState(
            nameError = "Name is required",
            priceError = "Enter a valid price",
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Name is required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a valid price").assertIsDisplayed()
    }

    @Test
    fun createModeShowsSaveAndNoDeleteButton() {
        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = AddSubscriptionState(), onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("New Subscription").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertDoesNotExist()
    }

    @Test
    fun editModeShowsFrozenTitleUpdateAndDeleteButtons() {
        val state = AddSubscriptionState(
            mode = AddSubscriptionMode.Edit(subscriptionId = 1L, originalName = "Netflix"),
            name = "Netflix Renamed",
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Netflix").assertIsDisplayed()
        composeTestRule.onNodeWithText("Netflix Renamed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Update").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
    }

    @Test
    fun updateConfirmationDialogIsShownWhenRequested() {
        val state = AddSubscriptionState(
            mode = AddSubscriptionMode.Edit(subscriptionId = 1L, originalName = "Netflix"),
            isUpdateConfirmationVisible = true,
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Update subscription?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save changes to \"Netflix\"?").assertIsDisplayed()
    }

    @Test
    fun deleteConfirmationDialogIsShownWhenRequested() {
        val state = AddSubscriptionState(
            mode = AddSubscriptionMode.Edit(subscriptionId = 1L, originalName = "Netflix"),
            isDeleteConfirmationVisible = true,
        )

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Delete subscription?").assertIsDisplayed()
        composeTestRule.onNodeWithText("This will permanently delete \"Netflix\". This can't be undone.").assertIsDisplayed()
    }
}
