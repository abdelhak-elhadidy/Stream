package com.vodbot.egy.extractors

import com.vodbot.egy.app
import com.vodbot.egy.utils.ExtractorApi
import com.vodbot.egy.utils.ExtractorLink
import com.vodbot.egy.utils.Qualities
import com.vodbot.egy.utils.getAndUnpack

class Mp4Upload : ExtractorApi() {
    override var name = "Mp4Upload"
    override var mainUrl = "https://www.mp4upload.com"
    private val srcRegex = Regex("""player\.src\("(.*?)"""")
    override val requiresReferer = true

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        with(app.get(url)) {
            getAndUnpack(this.text).let { unpackedText ->
                val quality = unpackedText.lowercase().substringAfter(" height=").substringBefore(" ").toIntOrNull()
                srcRegex.find(unpackedText)?.groupValues?.get(1)?.let { link ->
                    return listOf(
                        ExtractorLink(
                            name,
                            name,
                            link,
                            url,
                            quality ?: Qualities.Unknown.value,
                        )
                    )
                }
            }
        }
        return null
    }
}