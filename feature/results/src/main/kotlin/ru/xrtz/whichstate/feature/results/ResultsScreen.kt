package ru.xrtz.whichstate.feature.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.AtlasButton
import ru.xrtz.whichstate.core.designsystem.components.AtlasButtonVariant
import ru.xrtz.whichstate.core.designsystem.components.Hairline
import ru.xrtz.whichstate.core.designsystem.components.Overline
import ru.xrtz.whichstate.core.designsystem.components.ResultRow
import ru.xrtz.whichstate.core.designsystem.components.StatTile
import ru.xrtz.whichstate.core.designsystem.components.StatTileVariant
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.map.AccuracyMap

@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onAction: (ResultsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        ResultsUiState.Loading -> LoadingState(modifier)
        is ResultsUiState.Loaded -> LoadedResults(state, onAction, modifier)
    }
}

@Composable
private fun LoadingState(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(AtlasColors.paper),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.results_loading), style = AtlasType.mono(size = 13.sp), color = AtlasColors.muted)
    }
}

@Composable
private fun LoadedResults(
    state: ResultsUiState.Loaded,
    onAction: (ResultsAction) -> Unit,
    modifier: Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(AtlasColors.paper)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ResultsHeader(state)
            if (state.total > 0) {
                AccuracySection(state.accuracyByState, modifier = Modifier.padding(top = 30.dp))
            }
            StatsRow(state, modifier = Modifier.padding(top = 20.dp))
            ShareSection(state, onAction, modifier = Modifier.padding(top = 20.dp))
            CitiesSection(state, modifier = Modifier.padding(top = 34.dp))
        }
        Hairline()
        BottomActions(
            onAction,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ResultsHeader(state: ResultsUiState.Loaded) {
    Overline(stringResource(R.string.results_time_up))
    val rank = stringArrayResource(rankPhraseArrays[state.rankTier])[state.rankIndex]
    Text(
        text = rank.uppercase(),
        style = AtlasType.display(size = 34.sp, letterSpacing = (-0.01).em),
        color = AtlasColors.ink,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 14.dp),
    )
    Text(
        text = stringResource(R.string.results_score_line, state.correct, state.total),
        style = AtlasType.display(size = 44.sp, letterSpacing = 0.em),
        color = AtlasColors.cyan,
        modifier = Modifier.padding(top = 10.dp),
    )
    Text(
        text = stringResource(R.string.results_percentile_line, state.percentile),
        style = AtlasType.mono(size = 12.sp, letterSpacing = 0.06.em),
        color = AtlasColors.muted,
        modifier = Modifier.padding(top = 6.dp),
    )
    if (state.shameCityName != null && state.shamePickedState != null) {
        Text(
            text = stringResource(R.string.results_shame_line, state.shameCityName, state.shamePickedState),
            style = AtlasType.body(size = 14.sp, weight = FontWeight.W500),
            color = AtlasColors.red,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun AccuracySection(accuracyByState: Map<UsState, Float>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1080f / 700f)
                .clip(AtlasShapes.corner),
        ) {
            AccuracyMap(accuracy = accuracyByState, modifier = Modifier.fillMaxSize())
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendDot(AtlasColors.green, stringResource(R.string.results_legend_correct))
            LegendDot(AtlasColors.red, stringResource(R.string.results_legend_wrong))
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.width(10.dp).height(10.dp).clip(AtlasShapes.corner).background(color))
        Text(
            label.uppercase(),
            style = AtlasType.mono(size = 11.sp, letterSpacing = 0.12.em),
            color = AtlasColors.muted,
        )
    }
}

@Composable
private fun StatsRow(state: ResultsUiState.Loaded, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        StatTile(
            value = state.correct.toString(),
            label = stringResource(R.string.results_stat_correct),
            variant = StatTileVariant.CORRECT,
            modifier = Modifier.weight(1f),
        )
        StatTile(
            value = state.wrong.toString(),
            label = stringResource(R.string.results_stat_wrong),
            variant = StatTileVariant.WRONG,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ShareSection(
    state: ResultsUiState.Loaded,
    onAction: (ResultsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AtlasButton(
            text = if (state.isSharing) {
                stringResource(R.string.results_share_generating)
            } else {
                stringResource(R.string.results_share_button)
            },
            onClick = { onAction(ResultsAction.Share) },
            variant = AtlasButtonVariant.INK,
            enabled = !state.isSharing,
        )
        Text(
            text = stringResource(R.string.results_mastered_line, state.masteredCount, state.totalStates),
            style = AtlasType.mono(size = 11.sp, letterSpacing = 0.1.em),
            color = AtlasColors.muted,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun CitiesSection(state: ResultsUiState.Loaded, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Hairline(modifier = Modifier.padding(bottom = 18.dp))
        Text(
            text = stringResource(R.string.results_cities_heading, state.total),
            style = AtlasType.overline(),
            color = AtlasColors.muted,
        )
        if (state.cities.isEmpty()) {
            Text(
                text = stringResource(R.string.results_cities_empty),
                style = AtlasType.body(size = 14.sp),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 12.dp),
            )
        } else {
            Column(modifier = Modifier.padding(top = 6.dp)) {
                state.cities.forEach { city ->
                    ResultRow(cityName = city.cityName, stateText = city.stateText, variant = city.variant)
                }
            }
        }
    }
}

@Composable
private fun BottomActions(onAction: (ResultsAction) -> Unit, modifier: Modifier = Modifier) {
    val activity = LocalContext.current.findActivity()
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AtlasButton(
            text = stringResource(R.string.results_play_again),
            onClick = { onAction(ResultsAction.PlayAgain(activity)) },
            variant = AtlasButtonVariant.PRIMARY,
            modifier = Modifier.weight(1f),
        )
        AtlasButton(
            text = stringResource(R.string.results_change_level),
            onClick = { onAction(ResultsAction.ChangeLevel(activity)) },
            variant = AtlasButtonVariant.SECONDARY,
            modifier = Modifier.weight(1f),
        )
    }
}
