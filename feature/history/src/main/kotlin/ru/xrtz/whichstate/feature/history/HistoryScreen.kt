package ru.xrtz.whichstate.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.Hairline
import ru.xrtz.whichstate.core.designsystem.components.Overline
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.Level

private val MAX_CONTENT_WIDTH = 480.dp

@Composable
fun HistoryScreen(state: HistoryUiState, onAction: (HistoryAction) -> Unit, modifier: Modifier = Modifier) {
    when (state) {
        HistoryUiState.Loading -> LoadingState(modifier, onAction)
        is HistoryUiState.Loaded -> LoadedHistory(state, onAction, modifier)
    }
}

@Composable
private fun LoadingState(modifier: Modifier, onAction: (HistoryAction) -> Unit) {
    Column(modifier = modifier.fillMaxSize().background(AtlasColors.paper)) {
        HistoryHeader(onAction)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.history_loading),
                style = AtlasType.mono(size = 13.sp),
                color = AtlasColors.muted,
            )
        }
    }
}

@Composable
private fun LoadedHistory(state: HistoryUiState.Loaded, onAction: (HistoryAction) -> Unit, modifier: Modifier) {
    val selected = state.levels.single { it.level == state.selectedLevel }
    Column(modifier = modifier.fillMaxSize().background(AtlasColors.paper)) {
        HistoryHeader(onAction)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = MAX_CONTENT_WIDTH)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .navigationBarsPadding(),
            ) {
                LevelTabs(state.levels, state.selectedLevel) { onAction(HistoryAction.SelectLevel(it)) }
                BestCard(selected.best, modifier = Modifier.padding(top = 20.dp))
                RecentSection(selected.recent, modifier = Modifier.padding(top = 28.dp))
            }
        }
    }
}

@Composable
private fun HistoryHeader(onAction: (HistoryAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HistoryBackButton(onClick = { onAction(HistoryAction.Back) })
        Text(
            text = stringResource(R.string.history_title).uppercase(),
            style = AtlasType.display(size = 22.sp, letterSpacing = (-0.01).em),
            color = AtlasColors.ink,
        )
    }
}

@Composable
private fun HistoryBackButton(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.history_back),
        style = AtlasType.mono(size = 11.2.sp, letterSpacing = 0.16.em),
        color = AtlasColors.muted,
        modifier = Modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .clip(AtlasShapes.corner)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun LevelTabs(levels: List<LevelHistoryUiModel>, selected: Level, onSelect: (Level) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().selectableGroup(),
    ) {
        levels.forEach { level ->
            val isSelected = level.level == selected
            Text(
                text = level.displayName,
                style = AtlasType.mono(size = 13.sp, weight = if (isSelected) FontWeight.W500 else FontWeight.W400),
                color = if (isSelected) AtlasColors.cyan else AtlasColors.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .selectable(selected = isSelected, role = Role.RadioButton) { onSelect(level.level) }
                    .minimumInteractiveComponentSize()
                    .clip(AtlasShapes.corner)
                    .background(if (isSelected) AtlasColors.cyan.copy(alpha = 0.08f) else AtlasColors.card)
                    .border(1.dp, if (isSelected) AtlasColors.cyan else AtlasColors.hairline, AtlasShapes.corner)
                    .padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun BestCard(best: AttemptUiModel?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Overline(stringResource(R.string.history_best_label))
        if (best == null) {
            Text(
                text = stringResource(R.string.history_no_best_yet),
                style = AtlasType.body(size = 14.sp),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 10.dp),
            )
        } else {
            Text(
                text = stringResource(R.string.history_score_line, best.correct, best.total),
                style = AtlasType.display(size = 34.sp, letterSpacing = 0.em),
                color = AtlasColors.cyan,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.history_percentile_line, best.percentile),
                style = AtlasType.mono(size = 11.5.sp, letterSpacing = 0.06.em),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = best.dateText,
                style = AtlasType.mono(size = 11.sp, letterSpacing = 0.04.em),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RecentSection(recent: List<AttemptUiModel>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Hairline(modifier = Modifier.padding(bottom = 18.dp))
        Text(
            text = stringResource(R.string.history_recent_heading, recent.size),
            style = AtlasType.overline(),
            color = AtlasColors.muted,
        )
        if (recent.isEmpty()) {
            Text(
                text = stringResource(R.string.history_recent_empty),
                style = AtlasType.body(size = 14.sp),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 12.dp),
            )
        } else {
            Column(modifier = Modifier.padding(top = 6.dp)) {
                recent.forEach { AttemptRow(it) }
            }
        }
    }
}

@Composable
private fun AttemptRow(attempt: AttemptUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(attempt.dateText, style = AtlasType.mono(size = 11.5.sp), color = AtlasColors.muted)
        Text(
            text = stringResource(R.string.history_score_line, attempt.correct, attempt.total),
            style = AtlasType.mono(size = 13.sp, weight = FontWeight.W500, letterSpacing = 0.04.em),
            color = AtlasColors.ink,
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun HistoryScreenPreview() {
    AtlasTheme {
        HistoryScreen(
            state = HistoryUiState.Loaded(
                selectedLevel = Level.ROOKIE,
                levels = listOf(
                    LevelHistoryUiModel(
                        level = Level.ROOKIE,
                        displayName = "Rookie",
                        best = AttemptUiModel(
                            correct = 47,
                            total = 50,
                            percentile = 92,
                            dateText = "Sep 10, 2026, 8:14 PM",
                        ),
                        recent = listOf(
                            AttemptUiModel(
                                correct = 47,
                                total = 50,
                                percentile = 92,
                                dateText = "Sep 10, 2026, 8:14 PM",
                            ),
                            AttemptUiModel(
                                correct = 32,
                                total = 50,
                                percentile = 61,
                                dateText = "Sep 9, 2026, 6:02 PM",
                            ),
                        ),
                    ),
                    LevelHistoryUiModel(Level.EXPLORER, "Explorer", best = null, recent = emptyList()),
                    LevelHistoryUiModel(Level.MASTER, "Master", best = null, recent = emptyList()),
                ),
            ),
            onAction = {},
        )
    }
}
