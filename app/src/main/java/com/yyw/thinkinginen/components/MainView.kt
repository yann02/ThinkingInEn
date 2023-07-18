package com.yyw.thinkinginen.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.yyw.thinkinginen.MainViewModel
import com.yyw.thinkinginen.TAG
import com.yyw.thinkinginen.domain.Result
import com.yyw.thinkinginen.domain.data
import com.yyw.thinkinginen.entities.vo.ViewMessage
import com.yyw.thinkinginen.entities.vo.ViewSeason
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(model: MainViewModel, windowSize: WindowSizeClass, onClickSearch: () -> Unit = {}) {
    Log.d(TAG, "MainView windowSize:$windowSize")
    val seasons: List<ViewSeason> by model.mViewSeasons.collectAsState()
    val lastPosition: Result<Int> by model.mScrollPosition.collectAsState()
    val wrapMessages: Result<List<ViewMessage>> by model.mViewMessages.collectAsState()
    val drawerOpen by model.drawerShouldBeOpened.collectAsState()
    Log.d(TAG, "messages2 size:${wrapMessages.data?.size}")
    var scrollToPosition by rememberSaveable {
        mutableStateOf(-1)
    }
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
    Log.d(TAG, "seasons size:${seasons.size},lastPosition:$lastPosition")
    if (seasons.isNotEmpty() && lastPosition is Result.Success && wrapMessages is Result.Success) {
        Log.d(TAG, "进来了")
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                Log.d(TAG, "WindowWidthSizeClass.Compact")
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    modifier = Modifier.navigationBarsPadding(),
                    drawerContent = {
                        MyDrawerContent(
                            seasons,
                            seasonIdExpand = model.mSeasonIdExpand,
                            episodeId = model.mViewEpisodeId,
                            onSeasonClick = { seasonId ->
                                model.onSeasonClick(seasonId)
                            },
                            onEpisodeClick = { sId, eId ->
                                scope.launch {
                                    drawerState.close()
                                    scrollToPosition = model.onEpisodeClick(sId, eId)
                                }
                            },
                        )
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
                                    data = (wrapMessages as Result.Success).data,
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
                                model.mViewEpisodeName,
                                "Episode ${model.mViewEpisodeSort} / Season ${model.mViewSeasonId}",
                                onNavIconPressed = {
                                    model.openDrawer()
                                },
                                onSearch = {
                                    onClickSearch()
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                Log.d(TAG, "WindowWidthSizeClass.else")
                PermanentNavigationDrawer(drawerContent = {
                    MyDrawerContent(
                        seasons,
                        seasonIdExpand = model.mSeasonIdExpand,
                        episodeId = model.mViewEpisodeId,
                        onSeasonClick = { season ->
                            model.onSeasonClick(season)
                        },
                        onEpisodeClick = { sId, eId ->
                            scrollToPosition = model.onEpisodeClick(sId, eId)
//                                                scope.launch {
//                                                    drawerState.close()
//                                                    scrollToPosition = model.onEpisodeClick(sId, eId)
//                                                }
                        })
                }) {
                    Surface(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.navigationBars.only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                            )
                        )
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                        ) {
                            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                            Sentences(
                                data = (wrapMessages as Result.Success).data,
                                lastPosition = (lastPosition as Result.Success).data,
                                scrollToPosition = scrollToPosition,
                                modifier = Modifier
                                    .weight(1f)
                                    .navigationBarsPadding(),
                                onUpdateLastScrollPosition = model::updateLastScrollPosition,
                                onClickContent = model::onClickMessageById,
                            )
                        }
                    }
                }
            }
        }
    } else {
        Log.d(TAG, "没进来")
    }
}