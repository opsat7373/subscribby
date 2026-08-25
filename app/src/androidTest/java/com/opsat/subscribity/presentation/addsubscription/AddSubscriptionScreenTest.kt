package com.opsat.subscribity.presentation.addsubscription

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.opsat.subscribity.presentation.theme.SubscribityTheme
import org.junit.Rule
import org.junit.Test

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
    fun customPeriodShowsDaysField() {
        val state = AddSubscriptionState(periodOption = PeriodOption.CUSTOM)

        composeTestRule.setContent {
            SubscribityTheme {
                AddSubscriptionScreen(state = state, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Every N days").assertIsDisplayed()
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
}
