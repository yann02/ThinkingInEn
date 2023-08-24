package com.yyw.thinkinginen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yyw.thinkinginen.components.*
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "wyy"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(
        ExperimentalMaterial3WindowSizeClassApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
                                composable(
                                    "main",
                                ) {
                                    MainView(
                                        model = model,
                                        windowSize = windowSize,
                                        onClickSearch = { navController.navigate("search") })
                                }
                                composable("search") {
                                    SearchView(
                                        model = model,
                                        onBack = navController::popBackStack,
                                        onItemClick = { mId ->
                                            Log.d(TAG,"onItemClick mId:$mId")
                                            model.onResultItemClickForSearch(mId)
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        model.settingScrollPosition()
    }
}