/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.androiddevchallenge.ui.theme.MyTheme

@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

enum class TimerState {
    RUNNING,
    PAUSED,
    STOPPED,
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    var totalTime by remember { mutableStateOf(0.6f) }
    var remainingTime by remember { mutableStateOf(0L) }
    var timerState by remember { mutableStateOf(TimerState.STOPPED) }

    fun getTotalTime(): Long {
        return (totalTime * 600000).toInt().toLong()
    }

    val timer = object : CountDownTimer(getTotalTime(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTime = millisUntilFinished
        }
        override fun onFinish() {
            timerState = TimerState.STOPPED
        }
    }

    fun startTimer() {
        timerState = TimerState.RUNNING
        timer.start()
    }

    fun pauseTimer() {
        timerState = TimerState.PAUSED
        timer.cancel()
    }

    fun resetTimer() {
        timerState = TimerState.STOPPED
        timer.cancel()

        remainingTime = totalTime.toLong()
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(40.dp),

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "It's time for a TEA",
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.h1,
                )

                Spacer(Modifier.height(40.dp))

                if (timerState == TimerState.RUNNING) {
                    TimerBox(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val value by infiniteTransition.animateValue(
                            initialValue = 0.dp,
                            targetValue = (-100).dp,
                            typeConverter = Dp.VectorConverter,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )

                        TimerText(millis = remainingTime, modifier = Modifier.offset(y = value))
                        TimerText(millis = remainingTime, modifier = Modifier.offset(y = value + 100.dp))
                    }
                } else {
                    TimerBox(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        TimerText(millis = getTotalTime())
                    }
                }

                AnimatedVisibility(visible = timerState != TimerState.RUNNING) {
                    Slider(value = totalTime, onValueChange = { totalTime = it })
                }

                AnimatedVisibility(visible = timerState == TimerState.RUNNING) {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Row(Modifier.align(Alignment.CenterHorizontally)) {

                    Crossfade(targetState = timerState) { state ->
                        when (state) {
                            TimerState.RUNNING -> TimerButton(onClick = { pauseTimer() }, icon = Icons.Filled.Pause)
                            else -> TimerButton(onClick = { startTimer() }, icon = Icons.Filled.PlayArrow)
                        }
                    }

                    Spacer(Modifier.width(20.dp))

                    TimerButton(onClick = { resetTimer() }, icon = Icons.Filled.Replay)
                }

                Spacer(Modifier.height(80.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.tea),
                contentDescription = "Background Image",
                Modifier
                    .fillMaxWidth()
                    .scale(1.5f)
            )
        }
    }
}

@Composable
fun TimerBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clipToBounds()
            .border(width = 4.dp, color = MaterialTheme.colors.secondary)
            .padding(8.dp)
    ) {
        content()
    }
}

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    millis: Long,
) {
    val minutes = millis / 1000 / 60
    val seconds = millis / 1000 % 60
    val text = "%02d".format(minutes) + ":" + "%02d".format(seconds)

    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.h1,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
fun TimerButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.secondary,
            modifier = Modifier.size(60.dp),
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}
