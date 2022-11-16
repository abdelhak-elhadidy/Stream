package com.vodbot.egy.extractors

import com.vodbot.egy.utils.ExtractorLink
import com.vodbot.egy.app
import com.vodbot.egy.utils.*

open class Maxstream : ExtractorApi() {
    override var name = "Maxstream"
    override var mainUrl = "https://maxstream.video/"
    override val requiresReferer = false
    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val extractedLinksList: MutableList<ExtractorLink> = mutableListOf()
        val response = app.get(url).text
        val jstounpack = Regex("cript\">eval((.|\\n)*?)</script>").find(response)?.groups?.get(1)?.value
        val unpacjed = JsUnpacker(jstounpack).unpack()
        val extractedUrl = unpacjed?.let { Regex("""src:"((.|\n)*?)",type""").find(it) }?.groups?.get(1)?.value.toString()

        M3u8Helper.generateM3u8(
            name,
            extractedUrl,
            url,
            headers = mapOf("referer" to url)
        ).forEach { link ->
            extractedLinksList.add(link)
        }

        return extractedLinksList
    }
}