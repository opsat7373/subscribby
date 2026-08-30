package com.opsat.subscribity.presentation.subscriptionlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.opsat.subscribity.data.seed.SubscriptionSeedData
import com.opsat.subscribity.presentation.theme.SubscribityTheme
import org.junit.Rule
import org.junit.Test

class SubscriptionListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun seedSubscriptionsAreDisplayedCorrectly() {
        val items = SubscriptionSeedData.subscriptions.map { it.toUiModel() }
        composeTestRule.setContent {
            SubscribityTheme {
                SubscriptionListScreen(
                    state = SubscriptionListState(isLoading = false, subscriptions = items),
                    onIntent = {},
                    onAddClick = {},
                    onSettingsClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Subscriptions List").assertIsDisplayed()
        items.forEach { item ->
            composeTestRule.onNodeWithText(item.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(item.priceLabel).assertIsDisplayed()
            composeTestRule.onNodeWithText(item.nextPaymentDateLabel).assertIsDisplayed()
        }
    }

    @Test
    fun loadingStateShowsProgressIndicator() {
        composeTestRule.setContent {
            SubscribityTheme {
                SubscriptionListScreen(
                    state = SubscriptionListState(isLoading = true),
                    onIntent = {},
                    onAddClick = {},
                    onSettingsClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun emptyStateShowsPlaceholderText() {
        composeTestRule.setContent {
            SubscribityTheme {
                SubscriptionListScreen(
                    state = SubscriptionListState(isLoading = false, subscriptions = emptyList()),
                    onIntent = {},
                    onAddClick = {},
                    onSettingsClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No subscriptions yet").assertIsDisplayed()
    }
}
