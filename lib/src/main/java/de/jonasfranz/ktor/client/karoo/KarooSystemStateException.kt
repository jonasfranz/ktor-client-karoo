package de.jonasfranz.ktor.client.karoo

sealed class KarooSystemStateException(
    reason: String,
) : IllegalStateException(reason)

class KarooSystemNotConnectedException : KarooSystemStateException("Karoo System Service is not connected")

class KarooIsUnsupportedException : KarooSystemStateException("Karoo 2 does not support HTTP requests")
