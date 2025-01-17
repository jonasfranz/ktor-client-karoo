package de.jonasfranz.ktor.client.karoo

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.HardwareType
import io.hammerhead.karooext.models.HttpResponseState
import io.hammerhead.karooext.models.OnHttpResponse
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.callContext
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.util.toMap
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout


class KarooEngine(override val config: KarooEngineConfig) : HttpClientEngineBase("karoo") {

    private val karooSystem: KarooSystemService
        get() = config.karooSystemService

    @OptIn(FlowPreview::class)
    @InternalAPI
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        if (!karooSystem.connected) {
            throw KarooSystemNotConnectedException()
        }
        if (karooSystem.hardwareType == HardwareType.K2) throw KarooIsUnsupportedException()

        return callbackFlow {
            val callContext = callContext()
            val listenerId = karooSystem.addConsumer(
                params = OnHttpResponse.MakeHttpRequest(
                    method = data.method.value,
                    url = data.url.toString(),
                    body = data.body.toByteArray(),
                    headers = data.headers.toMap().mapValues { it.value.joinToString(",") },
                    waitForConnection = false,
                ),
                onError = { throw Exception(it) }
            ) { event: OnHttpResponse ->
                val state = event.state
                if (state is HttpResponseState.Complete) {
                    trySend(state.toHttpResponseData(callContext))
                }
            }
            awaitClose {
                karooSystem.removeConsumer(listenerId)
            }
        }
            .timeout(config.requestTimeout)
            .first()
    }


}