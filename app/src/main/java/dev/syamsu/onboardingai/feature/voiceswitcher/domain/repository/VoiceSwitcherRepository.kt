package dev.syamsu.onboardingai.feature.voiceswitcher.domain.repository

import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceItem
import java.io.File
import kotlinx.coroutines.flow.Flow

interface VoiceSwitcherRepository {
  fun getVoiceItems(): List<VoiceItem>

  suspend fun getVoiceAudio(voiceId: Int, sampleId: Int): File?

  fun getVoiceAudioStream(voiceId: Int, sampleId: Int): Flow<ByteArray>?
}
