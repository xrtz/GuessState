package ru.xrtz.whichstate.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.Hairline
import ru.xrtz.whichstate.core.designsystem.components.LevelCard
import ru.xrtz.whichstate.core.designsystem.components.Overline
import ru.xrtz.whichstate.core.designsystem.components.PartyModeCard
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode

@Composable
fun MenuScreen(
    state: MenuUiState,
    onAction: (MenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(AtlasColors.paper)) {
        if (maxWidth > maxHeight) {
            MenuScreenLandscape(state, onAction)
        } else {
            MenuScreenPortrait(state, onAction)
        }
        LanguageToggleButton(
            language = state.language,
            onClick = { onAction(MenuAction.ToggleLanguage) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 24.dp),
        )
        HistoryButton(
            onClick = { onAction(MenuAction.OpenHistory) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 24.dp),
        )
    }
}

@Composable
private fun MenuScreenPortrait(state: MenuUiState, onAction: (MenuAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeroSection()
        Hairline(modifier = Modifier.padding(top = 34.dp, bottom = 30.dp))
        LevelsSection(state.levels, onAction)
        PartySection(onAction)
        if (state.masteredCount != null) {
            MasteredLine(state.masteredCount, state.totalStates)
        }
    }
}

@Composable
private fun MenuScreenLandscape(state: MenuUiState, onAction: (MenuAction) -> Unit) {
    Row(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        Column(
            modifier = Modifier
                .weight(0.85f)
                .fillMaxHeight()
                .padding(start = 24.dp, end = 12.dp, top = 56.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HeroSection(compact = true)
        }
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 24.dp, top = 56.dp, bottom = 24.dp),
        ) {
            LevelsSection(state.levels, onAction)
            PartySection(onAction)
            if (state.masteredCount != null) {
                MasteredLine(state.masteredCount, state.totalStates)
            }
        }
    }
}

@Composable
private fun LanguageToggleButton(language: AppLanguage, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val switchTo = if (language == AppLanguage.ENGLISH) AppLanguage.RUSSIAN else AppLanguage.ENGLISH
    val label = if (switchTo == AppLanguage.RUSSIAN) "RU" else "EN"
    val description = stringResource(
        if (switchTo == AppLanguage.RUSSIAN) R.string.menu_switch_to_russian else R.string.menu_switch_to_english,
    )
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .size(40.dp)
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AtlasType.mono(size = 11.sp, weight = FontWeight.W500, letterSpacing = 0.06.em),
            color = AtlasColors.ink,
        )
    }
}

@Composable
private fun HeroSection(compact: Boolean = false) {
    FlankedEyebrow(stringResource(R.string.menu_eyebrow))

    Text(
        text = stringResource(R.string.menu_title),
        style = AtlasType.display(
            size = if (compact) 34.sp else 56.sp,
            letterSpacing = (-0.03).em,
            lineHeight = if (compact) 30.sp else 46.sp,
        ),
        color = AtlasColors.ink,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = if (compact) 16.dp else 22.dp),
    )
    Text(
        text = stringResource(R.string.menu_subtitle),
        style = AtlasType.body(size = if (compact) 13.sp else 15.sp),
        color = AtlasColors.muted,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .widthIn(max = if (compact) 260.dp else 280.dp)
            .padding(top = if (compact) 14.dp else 22.dp),
    )
}

@Composable
private fun LevelsSection(levels: List<LevelCardUiModel>, onAction: (MenuAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        levels.forEachIndexed { index, level ->
            LevelCard(
                rank = index + 1,
                name = level.displayName,
                meta = stringResource(R.string.menu_level_meta, level.cityCount),
                difficulty = level.difficulty,
                onClick = { onAction(MenuAction.SelectLevel(level.level)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PartySection(onAction: (MenuAction) -> Unit) {
    Overline(stringResource(R.string.menu_party_modes), modifier = Modifier.padding(top = 34.dp, bottom = 14.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PartyModeCard(
            name = stringResource(R.string.menu_party_describe_name),
            description = stringResource(R.string.menu_party_describe_description),
            onClick = { onAction(MenuAction.SelectPartyMode(PartyMode.DESCRIBE)) },
            modifier = Modifier.weight(1f),
        )
        PartyModeCard(
            name = stringResource(R.string.menu_party_guess_state_name),
            description = stringResource(R.string.menu_party_guess_state_description),
            onClick = { onAction(MenuAction.SelectPartyMode(PartyMode.GUESS_STATE)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MasteredLine(masteredCount: Int, totalStates: Int) {
    Hairline(modifier = Modifier.padding(top = 38.dp))
    Text(
        text = stringResource(R.string.menu_mastered_line, masteredCount, totalStates),
        style = AtlasType.mono(size = 10.sp, letterSpacing = 0.22.em),
        color = AtlasColors.muted,
        modifier = Modifier.padding(top = 14.dp),
    )
}

@Composable
private fun FlankedEyebrow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.width(32.dp).height(1.dp).background(AtlasColors.hairline))
        Overline(text, color = AtlasColors.cyan)
        Box(Modifier.width(32.dp).height(1.dp).background(AtlasColors.hairline))
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun MenuScreenPreview() {
    AtlasTheme {
        MenuScreen(
            state = MenuUiState(
                levels = listOf(
                    LevelCardUiModel(Level.ROOKIE, "Rookie", 50, 1),
                    LevelCardUiModel(Level.EXPLORER, "Explorer", 100, 2),
                    LevelCardUiModel(Level.MASTER, "Master", 200, 3),
                ),
                masteredCount = 7,
                totalStates = 50,
            ),
            onAction = {},
        )
    }
}
