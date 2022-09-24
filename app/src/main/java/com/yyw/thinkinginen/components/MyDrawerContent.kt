package com.yyw.thinkinginen.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yyw.thinkinginen.TAG
import com.yyw.thinkinginen.entities.vo.ViewSeason
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme


@Composable
fun MyDrawerContent(data: List<ViewSeason>, onSeasonClick: (ViewSeason) -> Unit, onEpisodeClick: (Int, Int) -> Unit) {
    Log.d(TAG, "MyDrawerContent")
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        data.forEach { season ->
            DrawerItemHeader(season.name, season.episodes.size.toString(), season.isOpen) {
                onSeasonClick(season)
            }
            AnimatedVisibility(visible = season.isOpen) {
                LazyColumn {
                    items(items = season.episodes, key = { episode -> episode.episodeId }) { episode ->
                        DrawerItemContent(
                            stringResource(
                                com.yyw.thinkinginen.R.string.drawer_content,
                                episode.sort,
                                episode.name
                            ), episode.messages.size.toString(), episode.current
                        ) {
                            onEpisodeClick(season.id, episode.episodeId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItemHeader(
    text: String,
    episodeNum: String = "0",
    isOpen: Boolean = false,
    onSeasonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onSeasonClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = episodeNum,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(imageVector = if(isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
        }
    }
}

@Composable
private fun DrawerItemContent(text: String, msgNum: String, selected: Boolean, onEpisodeClick: () -> Unit = {}) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable { onEpisodeClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 20.dp)
        )
        Text(
            text = msgNum,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 20.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreDrawerItemContent() {
    ThinkingInEnTheme {
        DrawerItemContent("Muddy Puddles", "58", true)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreDrawerItemHeader() {
    ThinkingInEnTheme {
        DrawerItemHeader(text = "Season 1")
    }
}
