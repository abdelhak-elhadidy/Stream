package com.vodbot.egy.extractors

import com.vodbot.egy.SubtitleFile
import com.vodbot.egy.app
import com.vodbot.egy.utils.ExtractorApi
import com.vodbot.egy.utils.ExtractorLink
import com.vodbot.egy.utils.M3u8Helper

open class Voe : ExtractorApi() {
    override val name = "Voe"
    override val mainUrl = "https://voe.sx"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val res = app.get(url, referer = referer).document
        val link = res.select("script").find { it.data().contains("const sources") }?.data()
            ?.substringAfter("\"hls\": \"")?.substringBefore("\",")

        M3u8Helper.generateM3u8(
            name,
            link ?: return,
            "$mainUrl/",
            headers = mapOf("Origin" to "$mainUrl/")
        ).forEach(callback)

    }
}