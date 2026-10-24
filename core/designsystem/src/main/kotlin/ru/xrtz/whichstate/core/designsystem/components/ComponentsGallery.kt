package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme

@Composable
fun ComponentsGallery(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(AtlasColors.paper)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ButtonsSection()
        CardsSection()
        ScoreAndStatsSection()
        ResultsListSection()
        StateTilesSection()
        InputsSection()
        Overline("Dividers")
        Hairline()
    }
}

@Composable
private fun ButtonsSection() {
    Overline("Buttons")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AtlasButton("Play again", {}, variant = AtlasButtonVariant.PRIMARY)
        AtlasButton("Change level", {}, variant = AtlasButtonVariant.SECONDARY)
        AtlasButton("Share result", {}, variant = AtlasButtonVariant.INK)
    }
}

@Composable
private fun CardsSection() {
    Overline("Cards")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LevelCard(
            rank = 1,
            name = "Rookie",
            meta = "50 cities · 30 sec",
            difficulty = 1,
            onClick = {},
            modifier = Modifier.weight(1f),
        )
        PartyModeCard(
            name = "Describe",
            description = "Describe the city — teammate guesses its name",
            onClick = {},
            modifier = Modifier.weight(1f),
        )
    }
    SwipeCard(cityName = "Springfield", stateText = "MO")
}

@Composable
private fun ScoreAndStatsSection() {
    Overline("Score and stats")
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        ScoreBadge(12, ScoreBadgeVariant.CORRECT)
        ScoreBadge(3, ScoreBadgeVariant.WRONG)
        RoundTimer(secondsLeft = 24, isWarning = false)
        RoundTimer(secondsLeft = 7, isWarning = true)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile("12", "Correct", variant = StatTileVariant.CORRECT, modifier = Modifier.weight(1f))
        StatTile("3", "Wrong", variant = StatTileVariant.WRONG, modifier = Modifier.weight(1f))
        StatTile("+9", "Net", variant = StatTileVariant.NET, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ResultsListSection() {
    Overline("Results list")
    Column {
        ResultRow("New York", "NY", ResultRowVariant.CORRECT)
        ResultRow("Springfield", "MA → MO", ResultRowVariant.WRONG)
        ResultRow("Fort Worth", "OK → TX", ResultRowVariant.NEAR_MISS)
    }
}

@Composable
private fun StateTilesSection() {
    Overline("State tiles")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StateTile("NH", status = StateTileStatus.DEFAULT)
        StateTile("VT", status = StateTileStatus.CORRECT)
        StateTile("MA", status = StateTileStatus.WRONG)
        StateTile("RI", status = StateTileStatus.NEAR_MISS)
    }
}

@Composable
private fun InputsSection() {
    var toggleChecked by remember { mutableStateOf(true) }
    var fieldValue by remember { mutableStateOf("") }

    Overline("Inputs")
    AtlasTextField(value = fieldValue, onValueChange = { fieldValue = it }, placeholder = "Team name")
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        AtlasToggle(checked = toggleChecked, onCheckedChange = { toggleChecked = it })
        AtlasToggle(checked = !toggleChecked, onCheckedChange = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1400)
@Composable
private fun ComponentsGalleryPreview() {
    AtlasTheme { ComponentsGallery(modifier = Modifier.fillMaxWidth()) }
}
