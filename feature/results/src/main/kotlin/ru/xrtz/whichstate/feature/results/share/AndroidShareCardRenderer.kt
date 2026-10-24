package ru.xrtz.whichstate.feature.results.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.map.asset.MapAssetDataSource
import ru.xrtz.whichstate.core.map.geometry.toPath
import ru.xrtz.whichstate.feature.results.R
import ru.xrtz.whichstate.feature.results.rankPhraseArrays
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.bodyTypeface
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.displayTypeface
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.fillPaint
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.monoTypeface
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.strokePaint
import ru.xrtz.whichstate.feature.results.share.ShareCardPaints.textPaint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val CARD_WIDTH = 1080
private const val CARD_HEIGHT = 1920
private const val MAP_TOP = 630f
private const val MAP_HEIGHT = 840f
private const val CACHE_DIR_NAME = "share"
private const val FILE_NAME = "which-state-result.png"

class AndroidShareCardRenderer @Inject constructor(
    @ApplicationContext private val context: Context,
) : ShareCardRenderer {
    override suspend fun render(summary: RoundSummary): Uri = withContext(Dispatchers.Default) {
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(AtlasColors.paper.toArgb())
        drawHeader(canvas, summary)
        drawMap(canvas, summary)
        drawLegend(canvas)
        drawFooter(canvas)
        saveToCache(bitmap)
    }

    private fun drawHeader(canvas: Canvas, summary: RoundSummary) {
        val centerX = CARD_WIDTH / 2f
        val hairline = strokePaint(AtlasColors.ink.copy(alpha = 0.18f).toArgb(), width = 2f)
        canvas.drawLine(centerX - 230f, 96f, centerX - 90f, 96f, hairline)
        canvas.drawLine(centerX + 90f, 96f, centerX + 230f, 96f, hairline)

        canvas.drawText(
            context.getString(R.string.results_share_brand).uppercase(),
            centerX,
            106f,
            textPaint(monoTypeface(context, 500), 30f, AtlasColors.cyan.toArgb(), letterSpacingEm = 0.22f),
        )
        val rank = context.resources.getStringArray(rankPhraseArrays[summary.rankTier])[summary.rankIndex]
        canvas.drawText(
            rank.uppercase(),
            centerX,
            250f,
            textPaint(displayTypeface(context), 80f, AtlasColors.ink.toArgb()),
        )
        canvas.drawText(
            context.getString(R.string.results_score_line, summary.correct, summary.correct + summary.wrong),
            centerX,
            430f,
            textPaint(displayTypeface(context), 150f, AtlasColors.cyan.toArgb()),
        )
        canvas.drawText(
            context.getString(R.string.results_percentile_line, summary.percentile).uppercase(),
            centerX,
            500f,
            textPaint(monoTypeface(context, 500), 36f, AtlasColors.muted.toArgb(), letterSpacingEm = 0.06f),
        )

        val shame = summary.worstMiss
        if (shame != null) {
            canvas.drawText(
                context.getString(R.string.results_shame_line, shame.question.city.name, shame.picked.name),
                centerX,
                562f,
                textPaint(bodyTypeface(context, 500), 34f, AtlasColors.red.toArgb()),
            )
        }
    }

    private suspend fun drawMap(canvas: Canvas, summary: RoundSummary) {
        val geometry = MapAssetDataSource(context).geometry()
        val scale = minOf(CARD_WIDTH / geometry.width, MAP_HEIGHT / geometry.height)
        val originX = CARD_WIDTH / 2f - geometry.width / 2f * scale
        val originY = MAP_TOP + MAP_HEIGHT / 2f - geometry.height / 2f * scale

        val greenFill = fillPaint(AtlasColors.green.toArgb())
        val redFill = fillPaint(AtlasColors.red.toArgb())
        val defaultFill = fillPaint(AtlasColors.mapFill.toArgb())
        val stroke = strokePaint(AtlasColors.ink.copy(alpha = 0.4f).toArgb(), width = 1.4f / scale)

        canvas.save()
        canvas.translate(originX, originY)
        canvas.scale(scale, scale)
        geometry.states.forEach { state ->
            val path = state.toPath().asAndroidPath()
            val ratio = state.usState?.let { summary.accuracyByState[it] }
            if (ratio == null) {
                canvas.drawPath(path, defaultFill)
            } else {
                canvas.save()
                canvas.clipPath(path)
                val splitY = state.bbox.top + state.bbox.height * ratio
                canvas.drawRect(state.bbox.left, state.bbox.top, state.bbox.right, splitY, greenFill)
                canvas.drawRect(state.bbox.left, splitY, state.bbox.right, state.bbox.bottom, redFill)
                canvas.restore()
            }
            canvas.drawPath(path, stroke)
        }
        canvas.restore()
    }

    private fun drawLegend(canvas: Canvas) {
        val centerX = CARD_WIDTH / 2f
        val y = 1520f
        val label = textPaint(monoTypeface(context, 500), 30f, AtlasColors.muted.toArgb()).apply {
            textAlign = Paint.Align.LEFT
        }

        val leftDotX = centerX - 150f
        canvas.drawCircle(leftDotX, y, 12f, fillPaint(AtlasColors.green.toArgb()))
        canvas.drawText(context.getString(R.string.results_legend_correct).uppercase(), leftDotX + 22f, y + 8f, label)

        val rightDotX = centerX + 40f
        canvas.drawCircle(rightDotX, y, 12f, fillPaint(AtlasColors.red.toArgb()))
        canvas.drawText(context.getString(R.string.results_legend_wrong).uppercase(), rightDotX + 22f, y + 8f, label)
    }

    private fun drawFooter(canvas: Canvas) {
        val centerX = CARD_WIDTH / 2f
        canvas.drawLine(
            centerX - 200f,
            1840f,
            centerX + 200f,
            1840f,
            strokePaint(AtlasColors.ink.copy(alpha = 0.13f).toArgb(), width = 2f),
        )
        canvas.drawText(
            context.getString(R.string.results_share_footer).uppercase(),
            centerX,
            1890f,
            textPaint(monoTypeface(context, 500), 28f, AtlasColors.muted.toArgb()),
        )
    }

    private suspend fun saveToCache(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, CACHE_DIR_NAME).apply { mkdirs() }
        val file = File(dir, FILE_NAME)
        FileOutputStream(file).use { stream -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
