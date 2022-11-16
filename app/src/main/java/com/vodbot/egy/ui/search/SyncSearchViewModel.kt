package com.vodbot.egy.ui.search

import com.vodbot.egy.SearchQuality
import com.vodbot.egy.SearchResponse
import com.vodbot.egy.TvType
import com.vodbot.egy.syncproviders.AccountManager.Companion.SyncApis

class SyncSearchViewModel {
    private val repos = SyncApis

    data class SyncSearchResultSearchResponse(
        override val name: String,
        override val url: String,
        override val apiName: String,
        override var type: TvType?,
        override var posterUrl: String?,
        override var id: Int?,
        override var quality: SearchQuality? = null,
        override var posterHeaders: Map<String, String>? = null,
    ) : SearchResponse
}