package de.jonasfranz.ktorclientkarooexample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.jonasfranz.ktor.client.karoo.Karoo
import io.hammerhead.karooext.KarooSystemService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.random.Random

data class MainViewState(
    val responseText: String = "n/a",
)

class MainViewModel(
    app: Application,
) : AndroidViewModel(app) {
    init {
        initEvents()
    }

    private val _uiState = MutableStateFlow(MainViewState())
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    private lateinit var karooSystem: KarooSystemService

    private val client: HttpClient by lazy {
        HttpClient(Karoo(karooSystem))
    }

    private fun initEvents() {
        viewModelScope.launch {
            suspendCancellableCoroutine { cont ->
                karooSystem = KarooSystemService(getApplication<Application>().applicationContext)
                karooSystem.connect()
                cont.invokeOnCancellation {
                    karooSystem.disconnect()
                }
            }
        }
    }

    fun sendRequest() {
        viewModelScope.launch {
            try {
                val response = client.get("https://api.sampleapis.com/wines/sparkling/${Random.nextInt(10)}")
                _uiState.update {
                    it.copy(
                        responseText = response.bodyAsText(),
                    )
                }
            } catch (err: Exception) {
                err.printStackTrace()
                _uiState.update {
                    it.copy(
                        responseText = err.message ?: "Unknown Error",
                    )
                }
            }
        }
    }
}
