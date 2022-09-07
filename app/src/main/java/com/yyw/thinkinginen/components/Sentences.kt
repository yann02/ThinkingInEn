package com.yyw.thinkinginen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yyw.thinkinginen.R
import com.yyw.thinkinginen.entities.Message

@Composable
fun Sentences(data: List<Message>, lastPosition: Int, modifier: Modifier, onUpdateLastScrollPosition: (Int) -> Unit) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = lastPosition)
    LazyColumn(
        state = listState,
        contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
        modifier = modifier.then(Modifier.fillMaxSize())
    ) {
        for (index in data.indices) {
            val prevRole = data.getOrNull(index - 1)?.role
            val prevEpisode = data.getOrNull(index - 1)?.eId
            val content = data[index]
            val isFirstMessageByRole = prevRole != content.role
            val isNewEpisode = content.eId != 1 && content.eId != prevEpisode
            item {
                if (isNewEpisode) {
                    Text(text = content.topic)
                }
                Sentence(content, isFirstMessageByRole)
            }
        }
    }
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    onUpdateLastScrollPosition(firstVisibleItemIndex)
}


@Composable
fun Sentence(msg: Message, isFirstMessageByRole: Boolean) {
    Row(
        modifier = Modifier
            .padding(8.dp)
    ) {
        if (isFirstMessageByRole) {
            Image(
                painter = painterResource(id = R.mipmap.timg), contentDescription = null, modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    )
//                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
        Column {
            if (isFirstMessageByRole) {
//                Text(text = msg.role, color = MaterialTheme.colors.secondaryVariant, style = MaterialTheme.typography.subtitle2)
                Text(
                    text = msg.role,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            var expanded by remember {
                mutableStateOf(false)
            }
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.clickable { expanded = !expanded }) {
                Column {
//                    Text(text = msg.content, style = MaterialTheme.typography.body2, modifier = Modifier.padding(8.dp))
                    Text(text = msg.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
                    AnimatedVisibility(visible = expanded) {
                        Text(
                            text = msg.cn,
//                            style = MaterialTheme.typography.body2,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                        )
                    }
                }
            }
        }
    }
}