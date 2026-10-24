package ru.xrtz.whichstate.feature.results

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.xrtz.whichstate.core.designsystem.components.ResultRowVariant
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.domain.model.Level

@Preview(showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun ResultsScreenPreview() {
    AtlasTheme {
        ResultsScreen(
            state = ResultsUiState.Loaded(
                level = Level.ROOKIE,
                rankTier = 4,
                rankIndex = 0,
                correct = 34,
                total = 50,
                percentile = 72,
                shameCityName = "Springfield",
                shamePickedState = "MA",
                accuracyByState = emptyMap(),
                wrong = 16,
                masteredCount = 12,
                totalStates = 50,
                cities = listOf(
                    ResultRowUiModel("Austin", "TX", ResultRowVariant.CORRECT),
                    ResultRowUiModel("Springfield", "MA → MO", ResultRowVariant.WRONG),
                ),
            ),
            onAction = {},
        )
    }
}
