package dev.syamsu.onboardingai.shared.network

import android.util.Log
import dev.syamsu.onboardingai.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

fun createKtorClient() =
    HttpClient(CIO) {
      install(Logging) {
        logger =
            object : Logger {
              override fun log(message: String) {
                if (BuildConfig.DEBUG) Log.d("ktor", message)
              }
            }
        level = LogLevel.HEADERS
      }
      install(HttpCache)
    }
