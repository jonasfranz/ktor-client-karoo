package de.jonasfranz.ktor.client.karoo.utils

import android.os.Bundle
import io.hammerhead.karooext.BUNDLE_PACKAGE
import io.hammerhead.karooext.BUNDLE_VALUE
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.aidl.IHandler
import io.hammerhead.karooext.aidl.IKarooSystem
import io.hammerhead.karooext.internal.KarooSystemListener
import io.hammerhead.karooext.internal.bundleWithSerializable
import io.hammerhead.karooext.internal.serializableFromBundle
import io.hammerhead.karooext.models.HardwareType
import io.hammerhead.karooext.models.KarooEvent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkConstructor
import org.jetbrains.annotations.ApiStatus.Internal
import org.junit.jupiter.api.Assertions.assertTrue

inline fun <reified T> KarooSystemService.mockEvent(crossinline onHandleEvent: (T) -> KarooEvent) {
    mockBundle()

    val listener = slot<KarooSystemListener>()
    val karooSystem = mockk<IKarooSystem>(relaxed = true)
    val bundle = slot<Bundle>()
    val handler = slot<IHandler>()
    every { karooSystem.addEventConsumer(any(), capture(bundle), capture(handler)) } just Runs

    every { packageName } returns "test"
    every { connected } returns true
    every { hardwareType } returns HardwareType.KAROO

    every {
        addConsumer(capture(listener))
    } answers {
        listener.captured.register(karooSystem)
        val event = bundle.captured.serializableFromBundle<T>()
        assertTrue(bundle.isCaptured)
        try {
            when {
                event == null -> throw Exception("EVENT IS NULL")
                else -> handler.captured.onNext(
                    onHandleEvent(
                        event,
                    ).bundleWithSerializable(packageName)
                )
            }
        } catch (exception: Throwable) {
            handler.captured.onError(exception.toString())
        } finally {
            unmockkConstructor(Bundle::class)
        }
        listener.captured.id
    }

    every { removeConsumer(any()) } just Runs
}

@Internal
fun mockBundle() {
    mockkConstructor(Bundle::class)
    val bundleValue = slot<String>()
    val packageValue = slot<String>()
    every { anyConstructed<Bundle>().getString(BUNDLE_VALUE) } answers { bundleValue.captured }
    every { anyConstructed<Bundle>().putString(BUNDLE_VALUE, capture(bundleValue)) } just Runs
    every { anyConstructed<Bundle>().getString(BUNDLE_PACKAGE) } answers { packageValue.captured }
    every { anyConstructed<Bundle>().putString(BUNDLE_PACKAGE, capture(packageValue)) } just Runs
}