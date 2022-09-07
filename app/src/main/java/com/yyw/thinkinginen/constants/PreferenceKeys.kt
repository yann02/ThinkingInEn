package com.yyw.thinkinginen.constants

import androidx.datastore.preferences.core.intPreferencesKey

object PreferenceKeys {
    const val NAME = "thinking"
    val SCROLL_POSITION = intPreferencesKey("scroll_position")
    val CURRENT_SEASON = intPreferencesKey("current_season")
    val CURRENT_EPISODE = intPreferencesKey("current_episode")
}