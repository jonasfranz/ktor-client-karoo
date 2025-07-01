package de.jonasfranz.ktor.client.karoo

import io.hammerhead.karooext.models.HttpResponseState
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HeadersImpl
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.toByteArray
import kotlin.coroutines.CoroutineContext

internal fun HttpResponseState.Complete.toHttpResponseData(callContext: CoroutineContext): HttpResponseData {
    val body = body?.run { ByteReadChannel(this) } ?: ByteReadChannel.Empty
    return HttpResponseData(
        statusCode = HttpStatusCode.fromValue(statusCode),
        headers = HeadersImpl(headers.mapValues { it.value.split(",") }),
        body = body,
        version = HttpProtocolVersion.HTTP_1_1,
        callContext = callContext,
        requestTime = GMTDate(),
    )
}

internal suspend fun OutgoingContent.toByteArray(): ByteArray =
    when (this) {
        is OutgoingContent.NoContent -> ByteArray(0)
        is OutgoingContent.ByteArrayContent -> bytes()
        is OutgoingContent.ReadChannelContent -> readFrom().toByteArray()
        is OutgoingContent.WriteChannelContent -> {
            val channel = ByteChannel()

            try {
                writeTo(channel)
                channel.close()
                channel.toByteArray()
            } catch (e: Exception) {
                channel.close()

                throw e
            }
        }
        else -> ByteArray(0)
    }
