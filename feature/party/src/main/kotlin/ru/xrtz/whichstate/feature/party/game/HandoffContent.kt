package ru.xrtz.whichstate.feature.party.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.AtlasButton
import ru.xrtz.whichstate.core.designsystem.components.Hairline
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.feature.party.R

@Composable
internal fun HandoffContent(
    state: PartyGameUiState.Handoff,
    onAction: (PartyGameAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.navigationBarsPadding().padding(24.dp)) {
        PartyBackButton(
            onClick = { onAction(PartyGameAction.ExitToMenu) },
            modifier = Modifier.align(Alignment.TopStart),
        )
        Column(
            modifier = Modifier.align(Alignment.Center).widthIn(max = 360.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.party_handoff_round, state.round, state.totalRounds).uppercase(),
                style = AtlasType.mono(size = 11.5.sp, letterSpacing = 0.24.em),
                color = AtlasColors.cyan,
            )
            Text(
                text = teamName(state.team).uppercase(),
                style = AtlasType.display(size = 52.sp, lineHeight = 52.sp),
                color = AtlasColors.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.party_handoff_hint),
                style = AtlasType.body(size = 15.sp),
                color = AtlasColors.muted,
                modifier = Modifier.padding(top = 8.dp),
            )
            Scoreboard(state.scoreboard, Modifier.padding(top = 28.dp))
            AtlasButton(
                text = stringResource(R.string.party_handoff_start),
                onClick = { onAction(PartyGameAction.StartTurn) },
                modifier = Modifier.padding(top = 30.dp),
            )
        }
    }
}

@Composable
private fun Scoreboard(rows: List<ScoreRowUiModel>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 18.dp, vertical = 6.dp),
    ) {
        rows.forEachIndexed { index, row ->
            if (index > 0) Hairline()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val color = if (row.isActive) AtlasColors.ink else AtlasColors.muted
                val weight = if (row.isActive) FontWeight.W600 else FontWeight.W400
                Text(teamName(row.team), style = AtlasType.body(size = 14.5.sp, weight = weight), color = color)
                Text(
                    stringResource(R.string.party_score_points, row.score),
                    style = AtlasType.body(size = 14.5.sp, weight = weight),
                    color = color,
                )
            }
        }
    }
}
