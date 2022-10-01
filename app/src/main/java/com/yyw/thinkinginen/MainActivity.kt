package com.yyw.thinkinginen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.components.*
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.Season
import com.yyw.thinkinginen.entities.vo.ViewMessage
import com.yyw.thinkinginen.entities.vo.ViewSeason
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
                    CompositionLocalProvider(LocalBackPressedDispatcher provides this@MainActivity.onBackPressedDispatcher) {
                        ThinkingInEnTheme {
                            val seasons: List<ViewSeason> by model.mViewSeasons.collectAsState()
                            val curSeason by model.mCurrentViewSeasonId.collectAsState()
                            val curEpisode by model.mCurrentViewEpisodeSort.collectAsState()
                            val lastPosition: Result<Int> by model.mScrollPosition.collectAsState()
                            val messages2: Result<List<ViewMessage>> by model.mViewMessages.collectAsState()
                            val episodeName by model.mCurrentViewEpisodeName.collectAsState()
                            val drawerOpen by model.drawerShouldBeOpened.collectAsState()
                            val scrollToPosition by model.mScrollToPosition.collectAsState()
                            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                            if (drawerOpen) {
                                // Open drawer and reset state in VM.
                                LaunchedEffect(Unit) {
                                    // wrap in try-finally to handle interruption whiles opening drawer
                                    try {
                                        drawerState.open()
                                    } finally {
                                        model.resetOpenDrawerAction()
                                    }
                                }
                            }
                            // Intercepts back navigation when the drawer is open
                            val scope = rememberCoroutineScope()
                            if (drawerState.isOpen) {
                                BackPressHandler {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            }
                            if (seasons.isNotEmpty() && lastPosition is Result.Success && messages2 is Result.Success) {
                                ModalNavigationDrawer(
                                    drawerState = drawerState,
                                    drawerContent = {
                                        MyDrawerContent(seasons, onSeasonClick = { season ->
                                            model.onSeasonClick(season)
                                        }, onEpisodeClick = { sId, eId ->
                                            scope.launch {
                                                drawerState.close()
                                                model.onEpisodeClick(sId, eId)
                                            }
                                        })
                                    }) {
                                    val topBarState = rememberTopAppBarState()
                                    val scrollBehavior =
                                        remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }
                                    Surface(
                                        modifier = Modifier.windowInsetsPadding(
                                            WindowInsets.navigationBars.only(
                                                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                            )
                                        )
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Column(
                                                Modifier
                                                    .fillMaxSize()
                                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                                            ) {
                                                Sentences(
                                                    data = (messages2 as Result.Success).data,
                                                    lastPosition = (lastPosition as Result.Success).data,
                                                    scrollToPosition = scrollToPosition,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .navigationBarsPadding(),
                                                    onUpdateLastScrollPosition = model::updateLastScrollPosition,
                                                    onClickContent = model::onClickMessageById
                                                )
                                            }
                                            MyAppBar(
                                                scrollBehavior,
                                                episodeName,
                                                "Episode $curEpisode / Season $curSeason"
                                            ) {
                                                model.openDrawer()
                                            }
                                        }
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
                for ((seasonIndex, s) in files.withIndex()) {
//                    Log.d("wyy", "s:$s")
                    val season = seasonIndex + 1
                    seasons.add(Season(season, "Season $season"))
                    val subFiles = assets.list("PeppaPig/$s")
                    subFiles?.sortBy {
                        it.split(".")[0].toInt()
                    }
                    if (!subFiles.isNullOrEmpty()) {
                        for ((episodeIndex, ss) in subFiles.withIndex()) {
                            //  seasonIndex * 1000用于区分不同的季，避免重复的episodeId
                            Log.d(TAG, "ss:$ss")
                            val episodeId = episodeIndex + 1 + seasonIndex * 1000
                            val sort = episodeIndex + 1
//                            Log.d("wyy", "ss:$ss")
                            val jsonString = assets.open("PeppaPig/$s/$ss").bufferedReader().use { it.readText() }
//                            Log.d("wyy", "jsonString:$jsonString")
                            val listCountryType = object : TypeToken<List<Message>>() {}.type
                            val temps: List<Message> = Gson().fromJson(jsonString, listCountryType)
                            res.addAll(temps)
                            episodes.add(Episode(episodeId, sort, temps[0].topic, season))
                        }
                    }
                }
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        model.insertData(seasons, episodes, res)
    }
}