package dev.syamsu.onboardingai.feature.voiceswitcher.data

import android.content.Context
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceItem
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.repository.VoiceSwitcherRepository
import dev.syamsu.onboardingai.shared.network.createKtorClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoiceSwitcherRepositoryImpl(private val context: Context) : VoiceSwitcherRepository {

  override fun getVoiceItems(): List<VoiceItem> {
    return listOf(
            "https://static.dailyfriend.ai/images/voices/meadow.svg",
            "https://static.dailyfriend.ai/images/voices/cypress.svg",
            "https://static.dailyfriend.ai/images/voices/iris.svg",
            "https://static.dailyfriend.ai/images/voices/hawke.svg",
            "https://static.dailyfriend.ai/images/voices/seren.svg",
            "https://static.dailyfriend.ai/images/voices/stone.svg",
        )
        .mapIndexed { index, s ->
          VoiceItem(
              id = index + 1,
              name =
                  s.substringAfterLast("/").substringBefore(".").replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                  },
              iconUri = s,
              isActive = false,
          )
        }
  }

  override suspend fun getVoiceAudio(voiceId: Int, sampleId: Int): File? =
      withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "voice")
        directory.mkdir()
        val tempFile = File(directory, "voice$voiceId-sample$sampleId.mp3")
        if (tempFile.length() > 1) return@withContext tempFile
        val response: HttpResponse =
            createKtorClient().use {
              it.get(
                  "https://static.dailyfriend.ai/conversations/samples/$voiceId/$sampleId/audio.mp3")
            }
        if (response.status.value in (200..<300)) {
          return@withContext null
        }
        val body = response.bodyAsBytes()
        if (tempFile.createNewFile()) {
          tempFile.writeBytes(body)
          return@withContext tempFile
        }
        return@withContext null
      }
}
