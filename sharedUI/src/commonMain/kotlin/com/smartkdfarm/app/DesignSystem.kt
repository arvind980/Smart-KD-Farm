package com.smartkdfarm.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -- Accurate Color Palette --
val AppDarkBg = Color(0xFF030C0B)
val AppGlassBg = Color(0xFF0A1F1C).copy(alpha = 0.6f)
val AppPrimary = Color(0xFF00D68F) // Vibrant Mint/Green
val AppPrimaryGlow = Color(0xFF00D68F).copy(alpha = 0.3f)

val CardBlue = Color(0xFF42A5F5)
val CardPurple = Color(0xFF9575CD)
val CardRed = Color(0xFFE57373)
val CardYellow = Color(0xFFFFB74D)

val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF8E9B9A)
val TextLight = Color(0xFFB0BEC5)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkColorScheme = darkColorScheme(
        primary = AppPrimary,
        onPrimary = Color.Black,
        background = AppDarkBg,
        surface = AppGlassBg,
        onBackground = TextWhite,
        onSurface = TextWhite,
        secondary = AppPrimary
    )

    val typography = Typography(
        headlineLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = TextWhite
        ),
        headlineMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextWhite
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = TextWhite
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextWhite
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = TextGray
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = TextGray
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = typography,
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            ),
        color = AppGlassBg,
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDarkBg)
    ) {
        // Subtle background gradient glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(AppPrimary.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )
        content()
    }
}
