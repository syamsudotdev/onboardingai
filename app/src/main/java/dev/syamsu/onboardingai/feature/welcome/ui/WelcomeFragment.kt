package dev.syamsu.onboardingai.feature.welcome.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.syamsu.onboardingai.feature.main.ui.MainActivity
import dev.syamsu.onboardingai.shared.theme.AiAppTheme
import dev.syamsu.onboardingai.shared.theme.Orange200
import dev.syamsu.onboardingai.shared.theme.Orange400
import dev.syamsu.onboardingai.shared.theme.Orange600
import java.io.File

class WelcomeFragment : Fragment() {

  private var player: ExoPlayer? = null
  private val welcomeVm by
      viewModels<WelcomeViewModel>(factoryProducer = { WelcomeViewModel.Factory })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    player = ExoPlayer.Builder(requireContext()).build()
    activity?.onBackPressedDispatcher?.addCallback(owner = this) {
      (activity as? MainActivity)?.navigateToVoiceSwitcherFragment()
    }
  }

  override fun onStop() {
    super.onStop()
    player?.stop()
    player?.release()
  }

  override fun onDestroy() {
    super.onDestroy()
    player = null
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    return createScreen()
  }

  private fun createScreen() =
      ComposeView(requireContext()).apply {
        setContent {
          AiAppTheme {
            Scaffold(modifier = Modifier.fillMaxSize().background(Orange200)) { padding ->
              WelcomeScreen(
                  transcription = welcomeVm.transcription, modifier = Modifier.padding(padding))
              if (welcomeVm.transcription != null) {
                playAudioFile(welcomeVm.voiceFile)
              }
            }
          }
        }
      }

  private fun playAudioFile(file: File?) {
    if (file == null) return
    val newMediaItem = MediaItem.fromUri(Uri.fromFile(file))
    if (player?.currentMediaItem == newMediaItem) return
    if (player?.isPlaying == true) player?.stop()
    player?.setMediaItem(newMediaItem)
    player?.repeatMode = Player.REPEAT_MODE_ALL
    player?.prepare()
    player?.play()
  }

  companion object {
    const val ARG_FILE = "file"
    const val ARG_VOICE_ID = "voice_id"
    const val ARG_SAMPLE_ID = "sample_id"

    fun newInstance(file: File, voiceId: Int, sampleId: Int) =
        WelcomeFragment().apply {
          arguments =
              Bundle().apply {
                putSerializable(ARG_FILE, file)
                putInt(ARG_VOICE_ID, voiceId)
                putInt(ARG_SAMPLE_ID, sampleId)
              }
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeScreen(transcription: String?, modifier: Modifier = Modifier) {
  ConstraintLayout(modifier = modifier.fillMaxSize()) {
    val activity = LocalActivity.current
    MediumTopAppBar(
        title = {},
        navigationIcon = {
          IconButton(
              onClick = {
                (activity as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
              }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
              }
        },
        modifier = Modifier.fillMaxWidth(),
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().constrainAs(createRef()) { top.linkTo(parent.top) }) {
          AnimatedIcon(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f))
          AnimatedVisibility(
              visible = transcription != null,
              enter = expandVertically(),
              exit = shrinkVertically()) {
                Text(
                    text = transcription ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
              }
        }

    AnimatedVisibility(
        visible = transcription != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier =
            Modifier.fillMaxWidth().constrainAs(createRef()) { bottom.linkTo(parent.bottom) }) {
          Buttons()
        }
  }
}

@Composable
private fun Buttons() {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Button(
        colors =
            ButtonDefaults.buttonColors()
                .copy(
                    containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        onClick = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
      val brush = Brush.verticalGradient(Pair(0f, Orange600), Pair(0.8f, Orange400))
      Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxWidth().background(brush, RoundedCornerShape(50))) {
            Text(
                text = "Login",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            )
          }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        colors =
            ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent, contentColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        onClick = {},
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, color = Orange600, shape = RoundedCornerShape(50)),
    ) {
      Text(
          text = "Register",
          color = Orange600,
          textAlign = TextAlign.Center,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
      )
    }
    Spacer(modifier = Modifier.height(12.dp))
  }
}

@Composable
private fun AnimatedIcon(modifier: Modifier = Modifier) {
  val composition by
      rememberLottieComposition(
          LottieCompositionSpec.Url("https://static.dailyfriend.ai/images/mascot-animation.json"))
  val animationState =
      animateLottieCompositionAsState(
          composition,
          iterations = LottieConstants.IterateForever,
      )
  LottieAnimation(composition, progress = { animationState.progress }, modifier = modifier)
}
