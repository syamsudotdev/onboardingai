package dev.syamsu.onboardingai.feature.voiceswitcher.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceSwitcherProps
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.repository.VoiceSwitcherRepository
import dev.syamsu.onboardingai.feature.voiceswitcher.data.VoiceSwitcherRepositoryImpl
import kotlinx.coroutines.launch
import kotlin.random.Random

class VoiceSwitcherViewModel(
    private val voiceRepository: VoiceSwitcherRepository
) : ViewModel() {
    var voiceSwitcherProps by mutableStateOf(VoiceSwitcherProps())
        private set

    init {
        voiceSwitcherProps =
            voiceSwitcherProps.copy(items = voiceRepository.getVoiceItems())
    }

    fun chooseVoiceName(name: String) {
        var chosenVoiceId: Int? = null
        viewModelScope.launch {
            val newItems = voiceSwitcherProps.items?.map {
                if (it.name == name) chosenVoiceId = it.id
                it.copy(isActive = it.name == name)
            }
            voiceSwitcherProps =
                voiceSwitcherProps.copy(items = newItems)
            val sampleId = Random.nextInt(1, 20)
            val file = chosenVoiceId?.let { voiceRepository.getVoiceAudio(it, sampleId) }
            if (file != null) {
                voiceSwitcherProps = voiceSwitcherProps.copy(
                    currentVoiceFile = file,
                    shouldPlayVoice = true,
                    currentVoiceId = chosenVoiceId,
                    currentSampleId = sampleId,
                )
            }
        }
    }

    fun onVoiceEnded() {
        voiceSwitcherProps = voiceSwitcherProps.copy(shouldPlayVoice = false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    ?: throw IllegalStateException("Context not available")
                val voiceSwitcherRepository = VoiceSwitcherRepositoryImpl(context)
                return@initializer VoiceSwitcherViewModel(voiceSwitcherRepository)
            }
        }
    }
}
