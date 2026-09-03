package com.opsat.subscribity.presentation.subscriptionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.opsat.subscribity.presentation.common.Plate
import com.opsat.subscribity.presentation.theme.ControlLabel
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.FigureM
import com.opsat.subscribity.presentation.theme.FigureXL
import com.opsat.subscribity.presentation.theme.MicroLabel

@Composable
fun SpendingSummaryCard(items: List<SpendingSummaryItemUiModel>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return

    val paper = MaterialTheme.colorScheme.background
    val pages = remember(items) { items.chunked(2) }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Plate(modifier = modifier) {
        Text(text = "PER MONTH", style = MicroLabel)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            SpendingPage(items = pages[page], paper = paper)
        }
        if (pages.size > 1) {
            Spacer(modifier = Modifier.height(Dimens.SectionGapSmall))
            Row {
                repeat(pages.size) { index ->
                    Spacer(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .width(Dimens.TickMarkWidth)
                            .height(Dimens.TickMarkHeight)
                            .background(
                                if (index == pagerState.currentPage) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    paper.copy(alpha = 0.3f)
                                },
                            ),
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(Dimens.SectionGapSmall))
            Row {
                Spacer(
                    modifier = Modifier
                        .width(Dimens.TickMarkWidth)
                        .height(Dimens.TickMarkHeight)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
private fun SpendingPage(items: List<SpendingSummaryItemUiModel>, paper: Color, modifier: Modifier = Modifier) {
    if (items.size == 1) {
        Column(modifier = modifier) {
            Text(text = items[0].amount, style = FigureXL)
            Text(text = items[0].currencyCode, style = ControlLabel, color = MaterialTheme.colorScheme.primary)
        }
    } else {
        Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column {
                Text(text = items[0].amount, style = FigureXL)
                Text(text = items[0].currencyCode, style = ControlLabel, color = MaterialTheme.colorScheme.primary)
            }
            Row(modifier = Modifier.padding(start = Dimens.FieldBlockPaddingLarge)) {
                Spacer(
                    modifier = Modifier
                        .width(Dimens.HairlineWeight)
                        .fillMaxHeight()
                        .background(paper),
                )
                Column(modifier = Modifier.padding(start = Dimens.FieldBlockPaddingLarge)) {
                    Text(text = "ALSO", style = MicroLabel)
                    Text(text = items[1].amount, style = FigureM)
                    Text(text = items[1].currencyCode, style = ControlLabel, color = paper.copy(alpha = 0.5f))
                }
            }
        }
    }
}
