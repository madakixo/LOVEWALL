package com.lovewall.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovewall.app.R // assume you have drawables

@Composable
fun OnboardingScreen(
    onPairClick: () -> Unit,
    onSetWallpaperClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    title = "Welcome to LoveWall",
                    description = "Turn your home screen into a living love note for you and your partner.",
                    imageRes = R.drawable.onboarding_heart, // add your drawable
                    buttonText = "Next"
                ) { /* next */ }

                1 -> OnboardingPage(
                    title = "Stay Connected Always",
                    description = "Send sticky notes, voice messages, stickers, and share location instantly — right from your wallpaper.",
                    imageRes = R.drawable.onboarding_messages,
                    buttonText = "Next"
                ) { /* next */ }

                2 -> OnboardingPage(
                    title = "Get Closer Than Ever",
                    description = "With mutual consent, share ambient sounds or plan surprises together.",
                    imageRes = R.drawable.onboarding_couple,
                    buttonText = if (pagerState.currentPage == 2) "Start Loving" else "Next",
                    isLast = true,
                    onAction = {
                        if (pagerState.currentPage == 2) onPairClick() else { /* next */ }
                    }
                )
            }
        }

        // Bottom indicators + action button
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / 3",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        // animate to next page
                    } else {
                        onSetWallpaperClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun OnboardingPage(
    title: String,
    description: String,
    imageRes: Int,
    buttonText: String,
    isLast: Boolean = false,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 48.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (isLast) {
            Spacer(modifier = Modifier.height(48.dp))
            OutlinedButton(onClick = onAction) {
                Text("Grant Permissions & Pair")
            }
        }
    }
}
