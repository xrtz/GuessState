package ru.xrtz.whichstate.feature.party.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import ru.xrtz.whichstate.core.designsystem.components.AtlasButtonVariant
import ru.xrtz.whichstate.core.designsystem.components.Hairline
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.feature.party.R

private val MEDALS = listOf("🥇", "🥈", "🥉")

@Composable
internal fun GameEndContent(
    state: PartyGameUiState.GameEnd,
    onAction: (PartyGameAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.navigationBarsPadding().padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.party_game_over).uppercase(),
            style = AtlasType.display(size = 40.sp),
            color = AtlasColors.ink,
            textAlign = TextAlign.Center,
        )
        Text(
            text = state.winner?.let { stringResource(R.string.party_winner, teamName(it)) }
                ?: stringResource(R.string.party_tie),
            style = AtlasType.mono(size = 14.5.sp, letterSpacing = 0.06.em),
            color = AtlasColors.cyan,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
        )
        Standings(state.standings)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.widthIn(max = 360.dp).padding(top = 32.dp),
        ) {
            AtlasButton(
                text = stringResource(R.string.party_play_again),
                onClick = { onAction(PartyGameAction.PlayAgain) },
                modifier = Modifier.weight(1f),
            )
            AtlasButton(
                text = stringResource(R.string.party_menu),
                onClick = { onAction(PartyGameAction.ExitToMenu) },
                variant = AtlasButtonVariant.SECONDARY,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Standings(rows: List<StandingRowUiModel>) {
    Column(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner),
    ) {
        rows.forEachIndexed { index, row ->
            if (index > 0) Hairline()
            val isFirst = row.place == 0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isFirst) AtlasColors.cyan.copy(alpha = 0.05f) else AtlasColors.card)
                    .padding(horizontal = 18.dp, vertical = 15.dp),
            ) {
                Text(MEDALS.getOrElse(row.place) { "" }, fontSize = 18.sp, modifier = Modifier.width(24.dp))
                Text(
                    text = teamName(row.team),
                    style = AtlasType.body(size = 15.sp, weight = FontWeight.W500),
                    color = if (isFirst) AtlasColors.cyan else AtlasColors.ink,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.party_score_points, row.score),
                    style = AtlasType.mono(size = 16.sp, weight = FontWeight.W500),
                    color = AtlasColors.cyan,
                )
            }
        }
    }
}
