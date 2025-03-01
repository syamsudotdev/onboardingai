package dev.syamsu.onboardingai.feature.voiceswitcher.domain.repository

import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceItem
import java.io.File

interface VoiceSwitcherRepository {
  fun getVoiceItems(): List<VoiceItem>

  suspend fun getVoiceAudio(voiceId: Int, sampleId: Int): File?
}
