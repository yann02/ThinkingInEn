package com.yyw.thinkinginen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()
    private val messages: List<Message> by lazy {
        getSentences()
    }
    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.mScrollPosition.collect { sp ->
                    if (sp is Result.Success) {
                        setContent {
                            ThinkingInEnTheme {
                                Conversation(sp.data, messages) {
                                    position = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        model.settingScrollPosition(position)
    }

    private fun getSentences(): List<Message> {
        return try {
            val files = assets.list("PeppaPig")
//            Log.d("wyy", "files:$files")
//            Log.d("wyy", "files.size:${files?.size}")
            if (!files.isNullOrEmpty()) {
                for (s in files) {
//                    Log.d("wyy", "s:$s")
                    val subFiles = assets.list("PeppaPig/$s")
                    if (!subFiles.isNullOrEmpty()) {
                        for (ss in subFiles) {
//                            Log.d("wyy", "ss:$ss")
                        }
                    }
                }
            }
            val jsonString = assets.open("PeppaPig/Season1/episode1.json").bufferedReader().use { it.readText() }
//            Log.d("wyy", "jsonString:$jsonString")
            val listCountryType = object : TypeToken<List<Message>>() {}.type
            Gson().fromJson(jsonString, listCountryType)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            emptyList()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Greeting(msg: Message, isFirstMessageByRole: Boolean) {
    Row(modifier = Modifier.padding(8.dp)) {
        if (isFirstMessageByRole) {
            Image(
                painter = painterResource(id = R.mipmap.timg), contentDescription = null, modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    )
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
        Column {
            if (isFirstMessageByRole) {
                Text(text = msg.role, color = MaterialTheme.colors.secondaryVariant, style = MaterialTheme.typography.subtitle2)
                Spacer(modifier = Modifier.height(2.dp))
            }
            var expanded by remember {
                mutableStateOf(false)
            }
            Surface(
                elevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.clickable { expanded = !expanded }) {
                Column {
                    Text(text = msg.content, style = MaterialTheme.typography.body2, modifier = Modifier.padding(8.dp))
                    AnimatedVisibility(visible = expanded) {
                        Text(
                            text = msg.cn,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Conversation(initPosition: Int, messages: List<Message>, setPosition: (Int) -> Unit) {
    val tempPosition = remember {
        mutableStateOf(initPosition)
    }
    Box {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        LazyColumn(state = listState) {
            for (index in messages.indices) {
                val prevRole = messages.getOrNull(index - 1)?.role
                val content = messages[index]
                val isFirstMessageByRole = prevRole != content.role
                item {
                    Greeting(content, isFirstMessageByRole)
                }
            }
        }
        Log.d("wyy", "firstVisibleItemIndex:${listState.firstVisibleItemIndex}")
        Log.d("wyy", "firstVisibleItemScrollOffset:${listState.firstVisibleItemScrollOffset}")
        setPosition(listState.firstVisibleItemIndex)
//        if (tempPosition != 0) {
        if (tempPosition.value != 0) {
            tempPosition.value = 0
            Log.d("wyy", "initPosition")
            coroutineScope.launch {
                listState.animateScrollToItem(initPosition)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ThinkingInEnTheme {
//        Greeting("Android")
    }
}