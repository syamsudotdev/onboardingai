package dev.syamsu.onboardingai.feature.voiceswitcher.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.syamsu.onboardingai.BuildConfig
import dev.syamsu.onboardingai.feature.main.ui.MainActivity
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceSwitcherCallback
import dev.syamsu.onboardingai.feature.voiceswitcher.domain.VoiceSwitcherProps
import dev.syamsu.onboardingai.shared.theme.AiAppTheme
import dev.syamsu.onboardingai.shared.theme.Orange400
import dev.syamsu.onboardingai.shared.theme.Orange600
import java.io.File

class VoiceSwitcherFragment : Fragment() {

    private var player: ExoPlayer? = null
    private val voiceSwitcherVm by viewModels<VoiceSwitcherViewModel>(
        factoryProducer = { VoiceSwitcherViewModel.Factory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(requireActivity()).build()
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    player?.clearMediaItems()
                    voiceSwitcherVm.onVoiceEnded()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                if (BuildConfig.DEBUG) {
                    Log.d("ExoPlayer", "${error.message}")
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player = null
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
        player?.release()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createScreen()
    }

    private fun createScreen() = ComposeView(requireContext()).apply {
        setContent {
            AiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val activity = LocalActivity.current
                    val callback =
                        remember {
                            VoiceSwitcherCallback(
                                onClickRadio = voiceSwitcherVm::chooseVoiceName,
                                onClickNext = {
                                    val currentProps = voiceSwitcherVm.voiceSwitcherProps
                                    if (activity is MainActivity &&
                                        currentProps.currentVoiceFile != null &&
                                        currentProps.currentVoiceId != null &&
                                        currentProps.currentSampleId != null
                                    ) {
                                        activity.navigateToOnboardedFragment(
                                            voiceFile = currentProps.currentVoiceFile,
                                            voiceId = currentProps.currentVoiceId,
                                            sampleId = currentProps.currentSampleId,
                                        )
                                    }
                                })
                        }
                    VoiceSwitcherScreen(
                        props = voiceSwitcherVm.voiceSwitcherProps,
                        callback = callback,
                        modifier = Modifier.padding(padding)
                    )
                    LaunchedEffect(
                        voiceSwitcherVm.voiceSwitcherProps.shouldPlayVoice,
                        voiceSwitcherVm.voiceSwitcherProps.currentVoiceFile,
                    ) {
                        if (voiceSwitcherVm.voiceSwitcherProps.shouldPlayVoice) {
                            playAudioFile(file = voiceSwitcherVm.voiceSwitcherProps.currentVoiceFile)
                        }
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
        player?.prepare()
        player?.play()
    }

    companion object {
        fun newInstance(): VoiceSwitcherFragment = VoiceSwitcherFragment()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VoiceSwitcherScreen(
    props: VoiceSwitcherProps,
    callback: VoiceSwitcherCallback,
    modifier: Modifier,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .constrainAs(createRef()) {
                    top.linkTo(parent.top)
                },
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Pick my voice", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
            AnimatedIcon(shouldPlay = props.shouldPlayVoice, modifier = Modifier.fillMaxWidth())
            Text(text = "Find the voice that resonates with you")
            Spacer(modifier = Modifier.height(24.dp))
            props.items?.let { items ->
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .selectableGroup()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    for (index in items.indices) {
                        val item = items[index]
                        val cardColor =
                            if (index % 2 == 1) Color.Yellow.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.1f)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.49f)
                                .background(cardColor, shape = RoundedCornerShape(8.dp))
                                .let {
                                    if (item.isActive)
                                        it.border(1.dp, color = Orange600, shape = RoundedCornerShape(8.dp))
                                    else
                                        it
                                }
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { callback.onClickRadio(item.name) }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                RadioButton(selected = item.isActive,
                                    colors = RadioButtonDefaults.colors().copy(selectedColor = Orange600),
                                    onClick = { callback.onClickRadio(item.name) })
                            }
                            val imageRequest = ImageRequest.Builder(LocalContext.current)
                                .decoderFactory(SvgDecoder.Factory())
                                .data(item.iconUri)
                                .build()
                            AsyncImage(
                                imageRequest,
                                contentDescription = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 100.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
        Button(
            colors = ButtonDefaults.buttonColors()
                .copy(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
            onClick = callback.onClickNext,
            enabled = props.currentVoiceFile != null,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(createRef()) {
                    bottom.linkTo(parent.bottom)
                },
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .let {
                        if (props.currentVoiceFile == null) {
                            it.background(Color.LightGray, RoundedCornerShape(50))
                        } else {
                            val brush = Brush.verticalGradient(
                                Pair(0f, Orange600), Pair(0.8f, Orange400)
                            )
                            it.background(brush, RoundedCornerShape(50))
                        }
                    }
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun AnimatedIcon(shouldPlay: Boolean, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://static.dailyfriend.ai/images/mascot-animation.json")
    )
    val animationState = animateLottieCompositionAsState(
        composition,
        isPlaying = shouldPlay,
        iterations = 1,
        restartOnPlay = true,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    )
    LottieAnimation(composition, progress = { animationState.progress }, modifier = modifier)
}
