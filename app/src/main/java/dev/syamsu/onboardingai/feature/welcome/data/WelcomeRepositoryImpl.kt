package dev.syamsu.onboardingai.feature.welcome.data

import dev.syamsu.onboardingai.feature.welcome.domain.WelcomeRepository
import dev.syamsu.onboardingai.shared.network.createKtorClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class WelcomeRepositoryImpl : WelcomeRepository {
    override suspend fun getTranscription(voiceId: Int, voiceSampleId: Int): String? {
        val response: HttpResponse =
            createKtorClient().use {
                it.get("https://static.dailyfriend.ai/conversations/samples/$voiceId/$voiceSampleId/transcription.txt")
            }
        return response.bodyAsText().takeIf { response.status.value in 200..<300 }
    }
}
