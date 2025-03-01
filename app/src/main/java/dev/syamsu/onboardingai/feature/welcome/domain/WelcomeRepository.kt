package dev.syamsu.onboardingai.feature.welcome.domain

interface WelcomeRepository {
  suspend fun getTranscription(voiceId: Int, voiceSampleId: Int): String?
}
