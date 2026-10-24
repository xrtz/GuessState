package ru.xrtz.whichstate.feature.party.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.AtlasButton
import ru.xrtz.whichstate.core.designsystem.components.AtlasTextField
import ru.xrtz.whichstate.core.designsystem.components.AtlasToggle
import ru.xrtz.whichstate.core.designsystem.components.Overline
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.feature.party.R

@Composable
fun PartySetupScreen(state: PartySetupUiState, onAction: (PartySetupAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasColors.paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.party_setup_back).uppercase(),
            style = AtlasType.mono(size = 12.sp, letterSpacing = 0.14.em),
            color = AtlasColors.muted,
            modifier = Modifier
                .clickable { onAction(PartySetupAction.Back) }
                .minimumInteractiveComponentSize()
                .padding(vertical = 4.dp),
        )
        SetupHeader(state, modifier = Modifier.padding(top = 24.dp, bottom = 32.dp))

        SectionLabel(stringResource(R.string.party_setup_team_count_label))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().selectableGroup(),
        ) {
            state.teamCountOptions.forEach { count ->
                TeamCountOption(
                    text = stringResource(R.string.party_setup_team_count_option, count),
                    selected = count == state.teamCount,
                    onClick = { onAction(PartySetupAction.SelectTeamCount(count)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SectionLabel(stringResource(R.string.party_setup_team_names_label), Modifier.padding(top = 26.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(state.teamCount) { index ->
                AtlasTextField(
                    value = state.teamNames[index],
                    onValueChange = { onAction(PartySetupAction.ChangeTeamName(index, it)) },
                    placeholder = stringResource(R.string.party_team_default, index + 1),
                )
            }
        }

        PenaltyRow(state.penaltyOnSkip, onAction, Modifier.padding(top = 26.dp))

        AtlasButton(
            text = stringResource(R.string.party_setup_start),
            onClick = { onAction(PartySetupAction.Start) },
            modifier = Modifier.padding(top = 34.dp),
        )
    }
}

@Composable
private fun SetupHeader(state: PartySetupUiState, modifier: Modifier = Modifier) {
    val (title, description) = when (state.mode) {
        PartyMode.DESCRIBE -> R.string.party_setup_title_describe to R.string.party_setup_desc_describe
        PartyMode.GUESS_STATE -> R.string.party_setup_title_guess_state to R.string.party_setup_desc_guess_state
    }
    Column(modifier) {
        Text(
            text = stringResource(title).uppercase(),
            style = AtlasType.display(size = 32.sp, lineHeight = 36.sp),
            color = AtlasColors.ink,
        )
        Text(
            text = stringResource(description),
            style = AtlasType.body(size = 15.sp),
            color = AtlasColors.muted,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(
                R.string.party_setup_meta,
                state.roundCount,
                state.citiesPerRound,
                state.secondsPerRound,
            ),
            style = AtlasType.mono(size = 12.sp, letterSpacing = 0.08.em),
            color = AtlasColors.cyan,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Overline(text, modifier = modifier.padding(bottom = 12.dp))
}

@Composable
private fun TeamCountOption(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = AtlasType.mono(size = 14.sp, weight = if (selected) FontWeight.W500 else FontWeight.W400),
        color = if (selected) AtlasColors.cyan else AtlasColors.ink,
        textAlign = TextAlign.Center,
        modifier = modifier
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .minimumInteractiveComponentSize()
            .clip(AtlasShapes.corner)
            .background(if (selected) AtlasColors.cyan.copy(alpha = 0.08f) else AtlasColors.card)
            .border(1.dp, if (selected) AtlasColors.cyan else AtlasColors.hairline, AtlasShapes.corner)
            .padding(vertical = 12.dp),
    )
}

@Composable
private fun PenaltyRow(penaltyOnSkip: Boolean, onAction: (PartySetupAction) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.party_setup_penalty_label),
                style = AtlasType.body(size = 15.sp),
                color = AtlasColors.ink,
                modifier = Modifier.weight(1f),
            )
            AtlasToggle(
                checked = penaltyOnSkip,
                onCheckedChange = { onAction(PartySetupAction.SetPenaltyOnSkip(it)) },
            )
        }
        Text(
            text = stringResource(
                if (penaltyOnSkip) R.string.party_setup_penalty_on else R.string.party_setup_penalty_off,
            ),
            style = AtlasType.body(size = 13.sp),
            color = AtlasColors.muted,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
