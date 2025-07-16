package com.server.zepsonconnect.modules.settings

interface Importer {
    fun import(data: Map<String, *>)
}