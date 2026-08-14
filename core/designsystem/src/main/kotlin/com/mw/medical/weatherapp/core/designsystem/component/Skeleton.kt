package com.mw.medical.weatherapp.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.medical.weatherapp.core.designsystem.theme.WeatherAppTheme

@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraSmall,
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeletonAlpha",
    )
    Box(
        modifier = modifier
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SkeletonPreview() {
    WeatherAppTheme {
        Skeleton(
            modifier = Modifier
                .padding(16.dp)
                .size(
                    width = 160.dp,
                    height = 16.dp,
                ),
        )
    }
}
