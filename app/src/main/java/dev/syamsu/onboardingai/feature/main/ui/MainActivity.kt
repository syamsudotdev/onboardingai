package dev.syamsu.onboardingai.feature.main.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.FragmentActivity
import dev.syamsu.onboardingai.feature.voiceswitcher.ui.VoiceSwitcherFragment
import dev.syamsu.onboardingai.feature.welcome.ui.WelcomeFragment
import dev.syamsu.onboardingai.shared.theme.Orange600
import java.io.File

class MainActivity : FragmentActivity() {

  private val fragmentContainerViewId = View.generateViewId()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Orange600.toArgb(), Orange600.toArgb()))
    val fragmentContainer =
        FrameLayout(this).apply {
          layoutParams =
              ViewGroup.LayoutParams(
                  FrameLayout.LayoutParams.MATCH_PARENT,
                  FrameLayout.LayoutParams.WRAP_CONTENT,
              )
          id = fragmentContainerViewId
        }
    setContentView(fragmentContainer)
    navigateToVoiceSwitcherFragment()
  }

  fun navigateToVoiceSwitcherFragment() {
    supportFragmentManager
        .beginTransaction()
        .replace(
            fragmentContainerViewId, VoiceSwitcherFragment.newInstance(), "VoiceSwitcherFragment")
        .commitAllowingStateLoss()
  }

  fun navigateToWelcomeFragment(voiceFile: File, voiceId: Int, sampleId: Int) {
    supportFragmentManager
        .beginTransaction()
        .replace(
            fragmentContainerViewId,
            WelcomeFragment.newInstance(voiceFile, voiceId, sampleId),
            "WelcomeFragment",
        )
        .commitAllowingStateLoss()
  }
}
