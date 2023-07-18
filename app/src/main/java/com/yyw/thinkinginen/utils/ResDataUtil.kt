package com.yyw.thinkinginen.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yyw.thinkinginen.TAG
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.ResData
import com.yyw.thinkinginen.entities.Season
import java.io.IOException

/**
 * 加载所有的会话数据，分别提炼出哪一季，哪一集和每一集的会话内容
 */
fun requireResData(context: Context): ResData {
    Log.d(TAG, "getSentences")
    val seasons = mutableListOf<Season>()
    val episodes = mutableListOf<Episode>()
    val res = mutableListOf<Message>()
    val assets = context.assets
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
                        val listMessageType = object : TypeToken<List<Message>>() {}.type
                        val temps: List<Message> = Gson().fromJson(jsonString, listMessageType)
                        res.addAll(temps)
                        episodes.add(Episode(episodeId, sort, temps[0].topic, season))
                    }
                }
            }
        }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
    }
    return ResData(seasons = seasons, episodes = episodes, messages = res)
}