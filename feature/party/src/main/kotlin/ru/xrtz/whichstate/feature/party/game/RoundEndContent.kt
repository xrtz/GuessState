package ru.xrtz.whichstate.feature.party.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.AtlasButton
import ru.xrtz.whichstate.core.designsystem.components.StatTile
import ru.xrtz.whichstate.core.designsystem.components.StatTileVariant
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.feature.party.R

@Composable
internal fun RoundEndContent(
    state: PartyGameUiState.RoundEnd,
    onAction: (PartyGameAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.navigationBarsPadding().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.party_round_over).uppercase(),
                style = AtlasType.display(size = 27.sp),
                color = AtlasColors.ink,
                modifier = Modifier.weight(1f),
            )
            PartyBackButton(onClick = { onAction(PartyGameAction.ExitToMenu) })
        }
        Text(
            text = teamName(state.team),
            style = AtlasType.mono(size = 13.5.sp, letterSpacing = 0.08.em),
            color = AtlasColors.cyan,
            modifier = Modifier.padding(top = 4.dp, bottom = 22.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                state.correct.toString(),
                stringResource(R.string.party_stat_correct),
                Modifier.weight(1f),
                StatTileVariant.CORRECT,
            )
            StatTile(
                state.skipped.toString(),
                stringResource(R.string.party_stat_skipped),
                Modifier.weight(1f),
                StatTileVariant.WRONG,
            )
            StatTile(
                state.pointsText,
                stringResource(R.string.party_stat_points),
                Modifier.weight(1f),
                StatTileVariant.NET,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 22.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            state.results.forEach { ResultRow(it) }
        }
        AtlasButton(
            text = stringResource(if (state.isLastTurn) R.string.party_final_scores else R.string.party_next_team),
            onClick = { onAction(PartyGameAction.NextTurn) },
        )
    }
}

@Composable
private fun ResultRow(row: PartyResultRowUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = if (row.correct) "✓" else "✗",
            style = AtlasType.mono(size = 14.sp),
            color = if (row.correct) AtlasColors.green else AtlasColors.red,
        )
        Text(
            text = row.cityName,
            style = AtlasType.body(size = 15.sp),
            color = if (row.correct) AtlasColors.ink else AtlasColors.muted,
            modifier = Modifier.weight(1f),
        )
        Text(row.stateCode, style = AtlasType.mono(size = 12.5.sp), color = AtlasColors.muted)
    }
}
