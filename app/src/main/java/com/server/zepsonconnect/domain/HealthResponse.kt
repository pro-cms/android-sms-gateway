package com.server.zepsonconnect.domain

import com.server.zepsonconnect.BuildConfig
import com.server.zepsonconnect.modules.health.domain.CheckResult
import com.server.zepsonconnect.modules.health.domain.HealthResult
import com.server.zepsonconnect.modules.health.domain.Status

class HealthResponse(
    healthResult: HealthResult,

    val version: String = BuildConfig.VERSION_NAME,
    val releaseId: Int = BuildConfig.VERSION_CODE,
) {
    val status: Status = healthResult.status
    val checks: Map<String, CheckResult> = healthResult.checks
}