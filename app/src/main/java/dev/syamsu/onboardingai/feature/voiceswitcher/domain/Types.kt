package dev.syamsu.onboardingai.feature.voiceswitcher.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.io.File

data class VoiceItem(
    val id: Int,
    val name: String,
    val iconUri: String,
    val isActive: Boolean,
)

@Immutable
@Stable
data class VoiceSwitcherProps(
    val items: List<VoiceItem>? = null,
    val currentVoiceFile: File? = null,
    val shouldPlayVoice: Boolean = false,
    val currentVoiceId: Int? = null,
    val currentSampleId: Int? = null,
)

@Immutable
@Stable
class VoiceSwitcherCallback(val onClickRadio: (name: String) -> Unit, val onClickNext: () -> Unit)
