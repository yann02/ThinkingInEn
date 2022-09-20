package com.yyw.thinkinginen.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yyw.thinkinginen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    episodeName: String,
    ancestorName: String,
    onNavIconPressed: () -> Unit = {}
) {
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val backgroundColor = lerp(
        backgroundColors.containerColor(colorTransitionFraction = 0f).value,
        backgroundColors.containerColor(colorTransitionFraction = 1f).value,
        FastOutLinearInEasing.transform(scrollBehavior.state.overlappedFraction)
    )
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )
    Box(modifier = Modifier.background(color = backgroundColor)) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = episodeName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = ancestorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.statusBarsPadding(),
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onNavIconPressed() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}