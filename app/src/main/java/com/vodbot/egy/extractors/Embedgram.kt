package com.vodbot.egy.extractors

import com.vodbot.egy.SubtitleFile
import com.vodbot.egy.app
import com.vodbot.egy.utils.ExtractorApi
import com.vodbot.egy.utils.ExtractorLink
import com.vodbot.egy.utils.getQualityFromName
import com.vodbot.egy.utils.httpsify

class Embedgram : ExtractorApi() {
    override val name = "Embedgram"
    override val mainUrl = "https://embedgram.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val document = app.get(url, referer = referer).document
        val link = document.select("video source:last-child").attr("src")
        val quality = document.select("video source:last-child").attr("title")
        callback.invoke(
            ExtractorLink(
                this.name,
                this.name,
                httpsify(link),
                "$mainUrl/",
                getQualityFromName(quality),
                headers = mapOf(
                    "Range" to "bytes=0-"
                )
            )
        )
    }
}