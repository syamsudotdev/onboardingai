package dev.syamsu.onboardingai.feature.welcome.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.syamsu.onboardingai.feature.welcome.data.WelcomeRepositoryImpl
import dev.syamsu.onboardingai.feature.welcome.domain.WelcomeRepository
import kotlinx.coroutines.launch
import java.io.File

class WelcomeViewModel(
    private val welcomeRepository: WelcomeRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val voiceFile: File?
        get() = savedStateHandle[WelcomeFragment.ARG_FILE]
    var transcription: String? by mutableStateOf(null)
        private set
    private val voiceSampleId: Int?
        get() = savedStateHandle[WelcomeFragment.ARG_SAMPLE_ID]
    private val voiceId: Int?
        get() = savedStateHandle[WelcomeFragment.ARG_VOICE_ID]

    init {
        val currentVoiceId = voiceId
        val currentVoiceSampleId = voiceSampleId
        if (currentVoiceId != null && currentVoiceSampleId != null) {
            viewModelScope.launch {
                transcription =
                    welcomeRepository.getTranscription(voiceId = currentVoiceId, voiceSampleId = currentVoiceSampleId)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = this.createSavedStateHandle()
                val welcomeRepository = WelcomeRepositoryImpl()
                WelcomeViewModel(welcomeRepository, savedStateHandle)
            }
        }
    }
}
