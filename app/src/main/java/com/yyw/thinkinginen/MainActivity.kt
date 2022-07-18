package com.yyw.thinkinginen

import android.annotation.SuppressLint
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
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()

    //    private val messages: List<Message> by lazy {
//        getSentences()
//    }
//    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSentences()
        setContent {
            ThinkingInEnTheme {
                Conversation(model)
            }
        }
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                model.mScrollPosition.collect { sp ->
//                    if (sp is Result.Success) {
//                        setContent {
//                            ThinkingInEnTheme {
//                                Conversation(sp.data, messages) {
//                                    position = it
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        model.settingScrollPosition()
    }

    private fun getSentences(): List<Message> {
        val res = mutableListOf<Message>()
        try {
            val files = assets.list("PeppaPig")
            Log.d("wyy", "files:$files")
            Log.d("wyy", "files.size:${files?.size}")
            if (!files.isNullOrEmpty()) {
                for (s in files) {
                    Log.d("wyy", "s:$s")
                    val subFiles = assets.list("PeppaPig/$s")
                    if (!subFiles.isNullOrEmpty()) {
                        for (ss in subFiles) {
                            Log.d("wyy", "ss:$ss")
                            val jsonString = assets.open("PeppaPig/$s/$ss").bufferedReader().use { it.readText() }
                            Log.d("wyy", "jsonString:$jsonString")
                            val listCountryType = object : TypeToken<List<Message>>() {}.type
                            val temps: List<Message> = Gson().fromJson(jsonString, listCountryType)
                            res.addAll(temps)
                        }
                    }
                }
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        model.insertMessages(res)
        return res
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Conversation(model: MainViewModel) {
    val messages: Result<List<Message>> by model.mMessages.collectAsState()
    val lastPosition: Result<Int> by model.mScrollPosition.collectAsState()
    if (messages is Result.Success && lastPosition is Result.Success) {
        if ((messages as Result.Success<List<Message>>).data.isNotEmpty()) {
            Box {
                val listState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()
                LazyColumn(state = listState) {
                    for (index in (messages as Result.Success<List<Message>>).data.indices) {
                        val prevRole = (messages as Result.Success<List<Message>>).data.getOrNull(index - 1)?.role
                        val content = (messages as Result.Success<List<Message>>).data[index]
                        val isFirstMessageByRole = prevRole != content.role
                        item {
                            Greeting(content, isFirstMessageByRole)
                        }
                    }
                }
                Log.d("wyy", "firstVisibleItemIndex:${listState.firstVisibleItemIndex}")
                Log.d("wyy", "firstVisibleItemScrollOffset:${listState.firstVisibleItemScrollOffset}")
                model.updateLastScrollPosition(listState.firstVisibleItemIndex)
                if ((lastPosition as Result.Success<Int>).data != 0) {
                    Log.d("wyy", "initPosition")
                    coroutineScope.launch {
                        listState.scrollToItem((lastPosition as Result.Success<Int>).data)
                    }
                }
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