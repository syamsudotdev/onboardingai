package dev.syamsu.onboardingai.feature.welcome.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.io.File

@Immutable
@Stable
data class WelcomeScreenProps(
    val transcription: String,
    val voiceFile: File,
)
