package com.opsat.subscribity.presentation.subscriptionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opsat.subscribity.domain.model.SubscriptionIconType
import com.opsat.subscribity.presentation.addsubscription.SimpleIconsCatalog
import com.opsat.subscribity.presentation.common.LedgerRow
import com.opsat.subscribity.presentation.common.LocalFileImage
import com.opsat.subscribity.presentation.theme.BodyRow
import com.opsat.subscribity.presentation.theme.ControlLabel
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.MicroLabel
import com.opsat.subscribity.presentation.theme.PlateLabel
import com.opsat.subscribity.presentation.theme.contrastingTextColor
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun SubscriptionListRoute(
    onNavigateToEditSubscription: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SubscriptionListEffect.NavigateToEditSubscription -> onNavigateToEditSubscription(effect.id)
            }
        }
    }

    SubscriptionListScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionListScreen(
    state: SubscriptionListState,
    onIntent: (SubscriptionListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "SUBSCRIPTIONS",
            style = PlateLabel,
            modifier = Modifier.padding(horizontal = Dimens.ScreenGutter, vertical = 16.dp),
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).testTag("loading_indicator"),
                )
                state.subscriptions.isEmpty() -> Text(
                    text = "No subscriptions yet",
                    style = BodyRow,
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    SpendingSummaryCard(
                        items = state.monthlySpending,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.ScreenGutter, vertical = 12.dp),
                    )
                    Column(modifier = Modifier.padding(horizontal = Dimens.ScreenGutter)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(text = "SERVICE · NEXT CHARGE", style = MicroLabel, modifier = Modifier.weight(1f))
                            Text(text = "AMOUNT", style = MicroLabel, textAlign = TextAlign.End)
                        }
                        HardRule()
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = Dimens.ScreenGutter),
                    ) {
                        itemsIndexed(state.subscriptions, key = { _, item -> item.id }) { index, item ->
                            var visible by remember(item.id) { mutableStateOf(false) }
                            LaunchedEffect(item.id) {
                                delay(minOf(index, 8) * 40L)
                                visible = true
                            }
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 },
                            ) {
                                LedgerRow(
                                    name = item.name,
                                    dateLabel = item.nextPaymentDateLabel,
                                    periodLabel = item.periodLabel,
                                    amount = item.priceLabel,
                                    emphasized = item.isDueSoon || item.isCustomCycle,
                                    icon = { SubscriptionIcon(item = item) },
                                    onClick = { onIntent(SubscriptionListIntent.SelectSubscription(item.id)) },
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(horizontal = Dimens.ScreenGutter)) {
                        HardRule()
                        SubscriptionListFooter(state = state)
                        HardRule()
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionListFooter(state: SubscriptionListState, modifier: Modifier = Modifier) {
    val total = state.monthlySpending.firstOrNull()
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Dimens.RowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${state.subscriptions.size} ACTIVE · LEDGER TOTAL",
            style = MicroLabel,
            modifier = Modifier.weight(1f),
        )
        if (total != null) {
            Text(text = "${total.amount} ${total.currencyCode}", style = ControlLabel, textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun HardRule(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.HardRuleWeight)
            .background(MaterialTheme.colorScheme.onBackground),
    )
}

@Composable
private fun SubscriptionIcon(item: SubscriptionListItemUiModel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(Dimens.ListRowIconSize),
        contentAlignment = Alignment.Center,
    ) {
        when (item.iconType) {
            SubscriptionIconType.LETTER -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(item.iconColor)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        color = contrastingTextColor(item.iconColor),
                    )
                }
            }

            SubscriptionIconType.BRAND -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    val resId = item.iconValue?.let { SimpleIconsCatalog.drawableResFor(it) }
                    if (resId != null) {
                        Image(
                            painter = painterResource(resId),
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.ListRowIconSize).padding(7.dp),
                        )
                    }
                }
            }

            SubscriptionIconType.PHOTO -> {
                val context = LocalContext.current
                item.iconValue?.let { relativePath ->
                    LocalFileImage(
                        file = File(context.filesDir, relativePath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
