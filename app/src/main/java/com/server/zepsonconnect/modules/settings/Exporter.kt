package com.server.zepsonconnect.modules.settings

interface Exporter {
    fun export(): Map<String, *>
}