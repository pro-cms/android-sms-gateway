package com.server.zepsonconnect.modules.health.domain

data class HealthResult(
    val status: Status,
    val checks: Map<String, CheckResult>
)
