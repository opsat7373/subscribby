package com.opsat.subscribity.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.RowAmount
import com.opsat.subscribity.presentation.theme.RowCaption
import com.opsat.subscribity.presentation.theme.RowName
import com.opsat.subscribity.presentation.theme.SubscribityTheme

/**
 * A ledger line: name/caption weighted left, amount right-aligned in tabular figures, a 1.dp
 * hairline closes the row at the bottom. [dateLabel]/[periodLabel] stay separate Text nodes
 * (not one joined caption string) so callers can still query the date label on its own.
 */
@Composable
fun LedgerRow(
    name: String,
    dateLabel: String,
    periodLabel: String,
    amount: String,
    emphasized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    val hairline = MaterialTheme.colorScheme.outlineVariant
    val captionColor = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.MinTouchTarget)
            .clickable(onClick = onClick)
            .drawBehind {
                val strokeWidth = Dimens.HairlineWeight.toPx()
                drawLine(
                    color = hairline,
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(vertical = Dimens.RowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(modifier = Modifier.size(Dimens.ListRowIconSize), contentAlignment = Alignment.Center) {
                icon()
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = RowName, color = MaterialTheme.colorScheme.onBackground)
            Row {
                Text(text = dateLabel, style = RowCaption, color = captionColor)
                Text(text = " · ", style = RowCaption, color = captionColor)
                Text(text = periodLabel.uppercase(), style = RowCaption, color = captionColor)
            }
        }
        Text(
            text = amount,
            style = RowAmount,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End,
        )
    }
}

@Preview(name = "Light")
@Composable
private fun LedgerRowPreviewLight() {
    SubscribityTheme(darkTheme = false) {
        LedgerRow(
            name = "Netflix",
            dateLabel = "Sep 5, 2026",
            periodLabel = "Monthly",
            amount = "$15.99",
            emphasized = true,
            onClick = {},
        )
    }
}

@Preview(name = "Dark")
@Composable
private fun LedgerRowPreviewDark() {
    SubscribityTheme(darkTheme = true) {
        LedgerRow(
            name = "Netflix",
            dateLabel = "Sep 5, 2026",
            periodLabel = "Monthly",
            amount = "$15.99",
            emphasized = false,
            onClick = {},
        )
    }
}
