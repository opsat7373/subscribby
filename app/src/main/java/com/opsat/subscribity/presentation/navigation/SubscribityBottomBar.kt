package com.opsat.subscribity.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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

private val BarHeight = 63.dp
private val DividerHeight = 1.dp
private val AddButtonSize = 49.5.dp
private val AddButtonCornerRadius = 13.5.dp
private val NavIconSize = 41.4.dp
private val AddIconSize = 27.dp

@Composable
fun SubscribityBottomBar(
    isListSelected: Boolean,
    onListClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = onAddClick,
                        shape = RoundedCornerShape(AddButtonCornerRadius),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(AddButtonSize),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add subscription",
                                modifier = Modifier.size(AddIconSize),
                            )
                        }
                    }
                }
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
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(NavIconSize))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
