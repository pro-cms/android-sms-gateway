package com.server.zepsonconnect.modules.orchestrator

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orchestratorModule = module {
    singleOf(::OrchestratorService)
}

val MODULE_NAME = "orchestrator"
