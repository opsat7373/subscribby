package com.opsat.subscribity.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.FigureXL
import com.opsat.subscribity.presentation.theme.PlateLabel
import com.opsat.subscribity.presentation.theme.SubscribityTheme

/** Inverted block: fills with [MaterialTheme.colorScheme.onBackground], content color flips to [MaterialTheme.colorScheme.background]. */
@Composable
fun Plate(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(Dimens.PlatePadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

@Preview(name = "Light")
@Composable
private fun PlatePreviewLight() {
    SubscribityTheme(darkTheme = false) {
        Plate {
            Text("SUBSCRIPTIONS", style = PlateLabel)
            Text("128.50", style = FigureXL)
        }
    }
}

@Preview(name = "Dark")
@Composable
private fun PlatePreviewDark() {
    SubscribityTheme(darkTheme = true) {
        Plate {
            Text("SUBSCRIPTIONS", style = PlateLabel)
            Text("128.50", style = FigureXL)
        }
    }
}
