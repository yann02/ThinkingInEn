package com.yyw.thinkinginen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yyw.thinkinginen.entities.SeasonWithEpisodeAndMessages


@Composable
fun MyDrawerContent(data: List<SeasonWithEpisodeAndMessages>) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        data.forEach { season ->
            DrawerItemHeader(season.season.name)
            LazyColumn {
                items(items = season.episodes, key = { episode -> episode.episode.episodeId }) { episode ->
                    DrawerItemContent(episode.episode.name)
                }
            }
        }
    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerItemContent(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 58.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
