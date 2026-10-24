@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package ru.xrtz.whichstate.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.R

object AtlasType {
    private fun variable(resId: Int, weight: FontWeight, wght: Int) = Font(
        resId = resId,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(wght)),
    )

    val displayFamily = FontFamily(variable(R.font.unbounded, FontWeight.W800, 800))
    val bodyFamily = FontFamily(
        variable(R.font.inter, FontWeight.W400, 400),
        variable(R.font.inter, FontWeight.W500, 500),
        variable(R.font.inter, FontWeight.W600, 600),
    )
    val monoFamily = FontFamily(
        variable(R.font.jetbrains_mono, FontWeight.W400, 400),
        variable(R.font.jetbrains_mono, FontWeight.W500, 500),
    )

    fun display(size: TextUnit, letterSpacing: TextUnit = (-0.02).em, lineHeight: TextUnit = size) = TextStyle(
        fontFamily = displayFamily,
        fontWeight = FontWeight.W800,
        fontSize = size,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
    )

    fun body(size: TextUnit = 15.sp, weight: FontWeight = FontWeight.W400) = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = weight,
        fontSize = size,
        lineHeight = size * 1.45f,
    )

    fun mono(size: TextUnit, weight: FontWeight = FontWeight.W400, letterSpacing: TextUnit = 0.04.em) = TextStyle(
        fontFamily = monoFamily,
        fontWeight = weight,
        fontSize = size,
        letterSpacing = letterSpacing,
    )

    fun overline(size: TextUnit = 11.sp, letterSpacing: TextUnit = 0.28.em) =
        mono(size = size, weight = FontWeight.W400, letterSpacing = letterSpacing)
}
