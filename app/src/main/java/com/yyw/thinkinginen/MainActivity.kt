package com.yyw.thinkinginen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!model.hasInit) {
            getSentences()
            model.upDataHasInit()
        }
        setContentView(
            ComposeView(this).apply {
                setContent {
                    CompositionLocalProvider(LocalBackPressedDispatcher provides this@MainActivity.onBackPressedDispatcher) {
                        ThinkingInEnTheme {
                            val windowSize = calculateWindowSizeClass(this@MainActivity)
                            val navController = rememberNavController()
                            NavHost(
                                navController = navController,
                                startDestination = "main",
                                modifier = Modifier.semantics {
                                    testTagsAsResourceId = true
                                }) {
                                composable("main") {
                                    MainView(
                                        model = model,
                                        windowSize = windowSize,
                                        onClickSearch = { navController.navigate("search") })
                                }
                                composable("search") {
                                    SearchView(
                                        model = model,
                                        onBack = { navController.navigate("main") })
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
        Log.d(TAG, "getSentences")
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
                            val jsonString =
                                assets.open("PeppaPig/$s/$ss").bufferedReader().use { it.readText() }
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