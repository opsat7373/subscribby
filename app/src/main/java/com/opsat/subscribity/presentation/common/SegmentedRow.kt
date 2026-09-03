package com.opsat.subscribity.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opsat.subscribity.presentation.theme.ControlLabel
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.SubscribityTheme

/** Equal-width cells inside a 1.dp Ink border; the selected cell fills Ink with a Paper label — a flip, never a tint. */
@Composable
fun <T> SegmentedRow(
    options: List<T>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String,
) {
    val ink = MaterialTheme.colorScheme.onBackground
    val paper = MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.SegmentedRowHeight)
            .border(width = Dimens.HairlineWeight, color = ink),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (index > 0) {
                            Modifier.border(width = Dimens.HairlineWeight, color = ink)
                        } else {
                            Modifier
                        },
                    )
                    .background(if (selected) ink else paper)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(index) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(option).uppercase(),
                    style = ControlLabel,
                    color = if (selected) paper else ink,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Composable
private fun SegmentedRowPreviewLight() {
    SubscribityTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            SegmentedRow(
                options = listOf("Wk", "Mo", "Qtr", "Yr", "Cust"),
                selectedIndex = 1,
                onSelect = {},
                label = { it },
            )
        }
    }
}

@Preview(name = "Dark")
@Composable
private fun SegmentedRowPreviewDark() {
    SubscribityTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            SegmentedRow(
                options = listOf("Wk", "Mo", "Qtr", "Yr", "Cust"),
                selectedIndex = 1,
                onSelect = {},
                label = { it },
            )
        }
    }
}
