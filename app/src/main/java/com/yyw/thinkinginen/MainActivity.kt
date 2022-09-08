package com.yyw.thinkinginen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.components.MyAppBar
import com.yyw.thinkinginen.components.MyDrawerContent
import com.yyw.thinkinginen.components.Sentences
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.domain.data
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.Season
import com.yyw.thinkinginen.entities.SeasonWithEpisodeAndMessages
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

const val TAG = "wyy"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        getSentences()
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    ThinkingInEnTheme {
                        val seasons: Result<List<SeasonWithEpisodeAndMessages>> by model.mSeasons.collectAsState()
                        val curSeason: Result<Int> by model.mCurrentSeason.collectAsState()
                        val curEpisode: Result<Int> by model.mCurrentEpisode.collectAsState()
                        val lastPosition: Result<Int> by model.mScrollPosition.collectAsState()
                        val messages: Result<List<Message>> by model.mMessages.collectAsState()
                        val episodeName by remember(seasons, curSeason, curEpisode) {
                            derivedStateOf {
                                try {
                                    seasons.data?.get(curSeason.data ?: 0)?.episodes?.get(
                                        curEpisode.data ?: 0
                                    )?.episode?.name ?: ""
                                } catch (e: Exception) {
                                    ""
                                }
                            }
                        }
                        if (seasons is Result.Success && curSeason is Result.Success && curEpisode is Result.Success && lastPosition is Result.Success && messages is Result.Success) {
                            ModalNavigationDrawer(drawerContent = {
                                MyDrawerContent((seasons as Result.Success<List<SeasonWithEpisodeAndMessages>>).data)
                            }) {
                                val topBarState = rememberTopAppBarState()
                                val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }
                                Surface(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                        ) {
                                            Sentences(
                                                data = (messages as Result.Success).data,
                                                lastPosition = (lastPosition as Result.Success).data,
                                                modifier = Modifier.weight(1f),
                                                onUpdateLastScrollPosition = model::updateLastScrollPosition
                                            )
                                        }
                                        MyAppBar(
                                            scrollBehavior,
                                            episodeName,
                                            "Episode ${(curEpisode as Result.Success).data + 1} / Season ${(curSeason as Result.Success).data + 1}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        model.settingScrollPosition()
    }

    private fun getSentences() {
        val seasons = mutableListOf<Season>()
        val episodes = mutableListOf<Episode>()
        val res = mutableListOf<Message>()
        try {
            val files = assets.list("PeppaPig")
//            Log.d("wyy", "files:$files")
//            Log.d("wyy", "files.size:${files?.size}")
            if (!files.isNullOrEmpty()) {
//                for (s in files) {
                for ((seasonIndex, s) in files.withIndex()) {
//                    Log.d("wyy", "s:$s")
                    val season = seasonIndex + 1
                    seasons.add(Season(season, "Season $season"))
                    val subFiles = assets.list("PeppaPig/$s")
                    if (!subFiles.isNullOrEmpty()) {
//                        for (ss in subFiles) {
                        for ((episodeIndex, ss) in subFiles.withIndex()) {
                            val episode = episodeIndex + 1
//                            Log.d("wyy", "ss:$ss")
                            val jsonString = assets.open("PeppaPig/$s/$ss").bufferedReader().use { it.readText() }
//                            Log.d("wyy", "jsonString:$jsonString")
                            val listCountryType = object : TypeToken<List<Message>>() {}.type
                            val temps: List<Message> = Gson().fromJson(jsonString, listCountryType)
                            res.addAll(temps)
                            episodes.add(Episode(episode, temps[0].topic, season))
                        }
                    }
                }
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        model.insertData(seasons, episodes, res)
//        model.insertMessages(res)
    }
}