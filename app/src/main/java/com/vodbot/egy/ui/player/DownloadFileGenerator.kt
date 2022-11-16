package com.vodbot.egy.ui.player

import com.vodbot.egy.AcraApplication.Companion.context
import com.vodbot.egy.R
import com.vodbot.egy.ui.player.PlayerSubtitleHelper.Companion.toSubtitleMimeType
import com.vodbot.egy.utils.ExtractorLink
import com.vodbot.egy.utils.ExtractorUri
import com.vodbot.egy.utils.VideoDownloadManager
import kotlin.math.max
import kotlin.math.min

class DownloadFileGenerator(
    private val episodes: List<ExtractorUri>,
    private var currentIndex: Int = 0
) : IGenerator {
    override val hasCache = false

    override fun hasNext(): Boolean {
        return currentIndex < episodes.size - 1
    }

    override fun hasPrev(): Boolean {
        return currentIndex > 0
    }

    override fun next() {
        if (hasNext())
            currentIndex++
    }

    override fun prev() {
        if (hasPrev())
            currentIndex--
    }

    override fun goto(index: Int) {
        // clamps value
        currentIndex = min(episodes.size - 1, max(0, index))
    }

    override fun getCurrentId(): Int? {
        return episodes[currentIndex].id
    }

    override fun getCurrent(offset: Int): Any? {
        return episodes.getOrNull(currentIndex + offset)
    }

    override fun getAll(): List<Any>? {
        return null
    }

    override suspend fun generateLinks(
        clearCache: Boolean,
        isCasting: Boolean,
        callback: (Pair<ExtractorLink?, ExtractorUri?>) -> Unit,
        subtitleCallback: (SubtitleData) -> Unit,
        offset: Int,
    ): Boolean {
        val meta = episodes[currentIndex + offset]
        callback(Pair(null, meta))

        context?.let { ctx ->
            val relative = meta.relativePath
            val display = meta.displayName

            if (display == null || relative == null) {
                return@let
            }
            VideoDownloadManager.getFolder(ctx, relative, meta.basePath)
                ?.forEach { file ->
                    val name = display.removeSuffix(".mp4")
                    if (file.first != meta.displayName && file.first.startsWith(name)) {
                        val realName = file.first.removePrefix(name)
                            .removeSuffix(".vtt")
                            .removeSuffix(".srt")
                            .removeSuffix(".txt")
                            .trim()
                            .removePrefix("(")
                            .removeSuffix(")")

                        subtitleCallback(
                            SubtitleData(
                                realName.ifBlank { ctx.getString(R.string.default_subtitles) },
                                file.second.toString(),
                                SubtitleOrigin.DOWNLOADED_FILE,
                                name.toSubtitleMimeType(),
                                emptyMap()
                            )
                        )
                    }
                }
        }

        return true
    }
}