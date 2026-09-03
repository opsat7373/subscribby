package com.opsat.subscribity.presentation.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opsat.subscribity.presentation.theme.Dimens
import com.opsat.subscribity.presentation.theme.SubscribityTheme

private const val AnimationDurationMillis = 150

/** 58×30.dp switch with a square knob — replaces the M3 Switch everywhere in this theme. */
@Composable
fun SquareSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val ink = MaterialTheme.colorScheme.onBackground
    val trackColor = if (checked) MaterialTheme.colorScheme.primary else Color.Transparent
    val knobColor = if (checked) MaterialTheme.colorScheme.onPrimary else ink.copy(alpha = 0.35f)
    val travel = Dimens.SwitchWidth - Dimens.SwitchInset * 2 - Dimens.SwitchKnobSize
    val knobOffsetX by animateDpAsState(
        targetValue = if (checked) travel else 0.dp,
        animationSpec = tween(AnimationDurationMillis),
        label = "squareSwitchKnob",
    )

    Box(
        modifier = modifier
            .sizeIn(minWidth = Dimens.SwitchWidth, minHeight = Dimens.MinTouchTarget)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.SwitchWidth, Dimens.SwitchHeight)
                .border(width = Dimens.HairlineWeight, color = ink)
                .background(trackColor),
        ) {
            Box(
                modifier = Modifier
                    .padding(Dimens.SwitchInset)
                    .offset(x = knobOffsetX)
                    .size(Dimens.SwitchKnobSize)
                    .background(knobColor),
            )
        }
    }
}

@Preview(name = "Light")
@Composable
private fun SquareSwitchPreviewLight() {
    SubscribityTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            SquareSwitch(checked = true, onCheckedChange = {})
        }
    }
}

@Preview(name = "Dark")
@Composable
private fun SquareSwitchPreviewDark() {
    SubscribityTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            SquareSwitch(checked = false, onCheckedChange = {})
        }
    }
}
