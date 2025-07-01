package de.jonasfranz.ktor.client.karoo

import io.hammerhead.karooext.KarooSystemService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory

class Karoo(
    private val karooSystemService: KarooSystemService,
) : HttpClientEngineFactory<KarooEngineConfig> {
    override fun create(block: KarooEngineConfig.() -> Unit): HttpClientEngine =
        KarooEngine(KarooEngineConfig(karooSystemService).apply(block))
}
