package com.yyw.thinkinginen.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yyw.thinkinginen.R
import com.yyw.thinkinginen.TAG
import com.yyw.thinkinginen.entities.vo.ViewMessage

@Composable
fun Sentences(
    data: List<ViewMessage>,
    lastPosition: Int,
    scrollToPosition: Int,
    modifier: Modifier,
    onUpdateLastScrollPosition: (Int) -> Unit,
    onClickContent: (ViewMessage) -> Unit
) {
    Log.d(TAG, "=====Sentences lastPosition:$lastPosition")
    Log.d(TAG, "=====Sentences scrollToPosition:$scrollToPosition")
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = lastPosition)
    if (scrollToPosition != -1) {
        LaunchedEffect(scrollToPosition) {
            listState.animateScrollToItem(scrollToPosition)
        }
    }
    LazyColumn(
        state = listState,
        contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
        modifier = modifier
            .then(Modifier.fillMaxSize())
            .testTag("tag_conversation")
    ) {
        Log.d(TAG,"LazyColumn")
        for (index in data.indices) {
            val prevRole = data.getOrNull(index - 1)?.role
            val prevEpisode = data.getOrNull(index - 1)?.eId
            val content = data[index]
            val isFirstMessageByRole = prevRole != content.role
            val isNewEpisode = content.eId != 1 && content.eId != prevEpisode
            item {
                if (isNewEpisode) {
                    SentenceHeader(text = content.topic)
                }
                Sentence(content, isFirstMessageByRole, onClickContent)
            }
        }
    }
    if (listState.isScrollInProgress) {
        onUpdateLastScrollPosition(listState.firstVisibleItemIndex)
    }
}

@Composable
fun SentenceHeader(text: String) {
    Column {
        Spacer(modifier = Modifier.height(integerResource(id = R.integer.header_margin_top).dp))
        Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(
                    horizontal = integerResource(
                        id = R.integer.horizontal_padding
                    ).dp,
                    vertical = integerResource(
                        id = R.integer.vertical_padding_of_header
                    ).dp
                )
            )
        }
        Spacer(modifier = Modifier.height(integerResource(id = R.integer.header_margin_bottom).dp))
    }
}


@Composable
fun Sentence(msg: ViewMessage, isFirstMessageByRole: Boolean, onClickContent: (ViewMessage) -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
    ) {
        if (isFirstMessageByRole) {
            Image(
                painter = painterResource(id = getDrawableResByRole(msg.role)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
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
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(modifier = Modifier.clickable { onClickContent(msg) }) {
//                    Text(text = msg.content, style = MaterialTheme.typography.body2, modifier = Modifier.padding(8.dp))
                    Text(
                        text = msg.content,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    AnimatedVisibility(visible = msg.vShowCn) {
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

fun getDrawableResByRole(role: String): Int = when (role) {
    "Peppa" -> R.mipmap.peppa_web_en
    "Daddy Pig" -> R.mipmap.daddy_pig_web_en
    "Mummy Pig" -> R.mipmap.mummy_pig_web_en
    "George" -> R.mipmap.george_web_en
    "Suzy Sheep" -> R.mipmap.suzy_sheep_web_en
    "Granny Pig" -> R.mipmap.granny_pig_web_en
    "Grandpa Pig" -> R.mipmap.grandpa_pig_web_en
    "Polly" -> R.mipmap.polly_web_en
    "Miss Rabbit" -> R.mipmap.miss_rabbit_cut_web_en
    "Outsider" -> R.mipmap.outsider_web_en
    "Madame Gazelle" -> R.mipmap.madame_gazelle_web_en
    "Danny Dog" -> R.mipmap.danny_dog_web_en
    "Tooth Fairy" -> R.mipmap.tooth_fairy
    "Suzy Sheep's mummy" -> R.mipmap.suzy_sheeps_mummy
    "Granddad Dog" -> R.mipmap.granddad_dog
    "Dr.Brown Bear" -> R.mipmap.dr_brown_bear
    "Candy Cat" -> R.mipmap.candy_cat
    "Rebecca Rabbit" -> R.mipmap.rebecca_rabbit
    "Pedro Pony" -> R.mipmap.pedro_pony
    "Chloe" -> R.mipmap.chloe
    "Television" -> R.mipmap.television
    "Sleepy Princess" -> R.mipmap.sleepy_princess
    "King Daddy" -> R.mipmap.king_daddy
    "Nobody Cat" -> R.mipmap.nobody_cat
    "Mr. Bull" -> R.mipmap.mr_bull
    "Helicopter" -> R.mipmap.helicopter
    "Computer" -> R.mipmap.computer
    "Richard Rabbit" -> R.mipmap.richard_rabbit
    "Mr. Pony" -> R.mipmap.mr_pony
    "Mr. Zebra" -> R.mipmap.mr_zebra
    "Mr. Rabbit" -> R.mipmap.mr_rabbit
    "Nobody Potato" -> R.mipmap.nobody_potato
    "Detective Potato" -> R.mipmap.detective_potato
    "Others" -> R.mipmap.others
    "Mr. Wolf" -> R.mipmap.mr_wolf
    "Mr. Bull's Lad" -> R.mipmap.mr_bulls_lad
    "Mr. Bull's Lads" -> R.mipmap.mr_bulls_lads
    "A Little Wolf" -> R.mipmap.a_little_wolf
    else -> R.mipmap.undefined_web_en
}
