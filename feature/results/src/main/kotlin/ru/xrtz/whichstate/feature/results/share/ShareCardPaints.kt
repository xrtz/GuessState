package ru.xrtz.whichstate.feature.results.share

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import ru.xrtz.whichstate.core.designsystem.R as DesignSystemR

internal object ShareCardPaints {
    fun displayTypeface(context: Context): Typeface =
        variableTypeface(context, DesignSystemR.font.unbounded, weight = 800)

    fun bodyTypeface(context: Context, weight: Int): Typeface =
        variableTypeface(context, DesignSystemR.font.inter, weight)

    fun monoTypeface(context: Context, weight: Int): Typeface =
        variableTypeface(context, DesignSystemR.font.jetbrains_mono, weight)

    fun textPaint(typeface: Typeface, size: Float, colorInt: Int, letterSpacingEm: Float = 0f) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = size
            color = colorInt
            textAlign = Paint.Align.CENTER
            letterSpacing = letterSpacingEm
        }

    fun fillPaint(colorInt: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorInt }

    fun strokePaint(colorInt: Int, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorInt
        style = Paint.Style.STROKE
        strokeWidth = width
    }

    private fun variableTypeface(context: Context, fontResId: Int, weight: Int): Typeface {
        val family = ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT
        return Typeface.create(family, weight, false)
    }
}
