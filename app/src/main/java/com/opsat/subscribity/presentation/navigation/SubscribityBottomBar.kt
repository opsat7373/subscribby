package com.opsat.subscribity.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private val FabSize = 56.dp
private val BarHeight = 72.dp
private val DividerHeight = 1.dp
private val BarTotalHeight = BarHeight + DividerHeight

@Composable
fun SubscribityBottomBar(
    isListSelected: Boolean,
    onListClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BarTotalHeight + FabSize / 2),
    ) {
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = DividerHeight)
            Surface(color = MaterialTheme.colorScheme.background) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(BarHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BottomBarItem(
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "List",
                        selected = isListSelected,
                        onClick = onListClick,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BottomBarItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        selected = !isListSelected,
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(FabSize),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add subscription")
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier
            .selectable(selected = selected, onClick = onClick, role = Role.Tab)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = tint)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
