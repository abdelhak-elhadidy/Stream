package com.vodbot.egy.subtitles

import androidx.annotation.WorkerThread
import com.vodbot.egy.subtitles.AbstractSubtitleEntities.SubtitleEntity
import com.vodbot.egy.subtitles.AbstractSubtitleEntities.SubtitleSearch
import com.vodbot.egy.syncproviders.AuthAPI

interface AbstractSubProvider {
    @WorkerThread
    suspend fun search(query: SubtitleSearch): List<SubtitleEntity>? {
        throw NotImplementedError()
    }

    @WorkerThread
    suspend fun load(data: SubtitleEntity): String? {
        throw NotImplementedError()
    }
}

interface AbstractSubApi : AbstractSubProvider, AuthAPI