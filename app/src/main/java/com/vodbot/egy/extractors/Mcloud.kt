package com.vodbot.egy.extractors

open class Mcloud : WcoStream() {
    override var name = "Mcloud"
    override var mainUrl = "https://mcloud.to"
    override val requiresReferer = true
}