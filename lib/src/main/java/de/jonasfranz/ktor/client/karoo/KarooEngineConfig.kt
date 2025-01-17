package de.jonasfranz.ktor.client.karoo

import io.hammerhead.karooext.KarooSystemService
import io.ktor.client.engine.HttpClientEngineConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KarooEngineConfig(
    var karooSystemService: KarooSystemService,
): HttpClientEngineConfig() {

    /**
     * Specifies a maximum duration a HTTP request is allowed to take.
     */
    var requestTimeout: Duration = 10.seconds
}