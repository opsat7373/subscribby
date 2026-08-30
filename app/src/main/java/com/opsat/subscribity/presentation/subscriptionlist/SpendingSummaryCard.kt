package com.opsat.subscribity.presentation.subscriptionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SpendingSummaryCard(items: List<SpendingSummaryItemUiModel>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return

    val pages = remember(items) { items.chunked(2) }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                SpendingPage(
                    items = pages[page],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }
            if (pages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pages.size) { index ->
                        val active = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (active) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    },
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendingPage(items: List<SpendingSummaryItemUiModel>, modifier: Modifier = Modifier) {
    if (items.size == 1) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            SpendingAmount(items[0])
        }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
            items.forEach { item ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    SpendingAmount(item)
                }
            }
        }
    }
}

@Composable
private fun SpendingAmount(item: SpendingSummaryItemUiModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = item.amountLabel,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "/ month", style = MaterialTheme.typography.bodySmall)
    }
}
