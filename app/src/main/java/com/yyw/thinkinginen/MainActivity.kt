package com.yyw.thinkinginen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("wyy", "onCreate")
        val messages: List<Message> = getSentences()
        Log.d("wyy", "sentences:$messages")
        setContent {
            ThinkingInEnTheme {
                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
                Conversation(getSentences())
            }
        }
    }

    private fun getSentences(): List<Message> {
        return try {
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
                        }
                    }
                }
            }
            val jsonString = assets.open("PeppaPig/Season1/episode1.json").bufferedReader().use { it.readText() }
            Log.d("wyy", "jsonString:$jsonString")
            val listCountryType = object : TypeToken<List<Message>>() {}.type
            Gson().fromJson(jsonString, listCountryType)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            emptyList()
        }
    }
}

@Composable
fun Greeting(msg: Message) {
    Row(modifier = Modifier.padding(8.dp)) {
        Image(
            painter = painterResource(id = R.mipmap.timg), contentDescription = null, modifier = Modifier
                .size(40.dp)
                .clip(
                    CircleShape
                )
                .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = msg.role, color = MaterialTheme.colors.secondaryVariant, style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.height(2.dp))
            Surface(elevation = 1.dp, shape = MaterialTheme.shapes.medium) {
                Text(text = msg.content, style = MaterialTheme.typography.body2, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { msg ->
            Greeting(msg)
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