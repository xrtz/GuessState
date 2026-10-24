package ru.xrtz.whichstate.feature.party

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.feature.party.game.PartyGameScreen
import ru.xrtz.whichstate.feature.party.game.PartyGameUiState
import ru.xrtz.whichstate.feature.party.game.PartyResultRowUiModel
import ru.xrtz.whichstate.feature.party.game.ScoreRowUiModel
import ru.xrtz.whichstate.feature.party.game.StandingRowUiModel
import ru.xrtz.whichstate.feature.party.game.TeamLabel
import ru.xrtz.whichstate.feature.party.setup.PartySetupScreen
import ru.xrtz.whichstate.feature.party.setup.PartySetupUiState

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h860dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PartyScreensScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val owls = TeamLabel(2, "Owls")
    private val team1 = TeamLabel(1, null)

    private fun capture(content: @Composable () -> Unit) {
        composeTestRule.setContent { AtlasTheme { content() } }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun setup() = capture {
        val state = PartySetupUiState.initial(PartyMode.DESCRIBE).let {
            it.copy(teamCount = 3, teamNames = listOf("", "Owls", "", ""), penaltyOnSkip = true)
        }
        PartySetupScreen(state, onAction = {}, modifier = Modifier.fillMaxSize())
    }

    @Test
    fun handoff() = capture {
        val state = PartyGameUiState.Handoff(
            round = 2,
            totalRounds = 3,
            team = owls,
            scoreboard = listOf(ScoreRowUiModel(team1, 4, false), ScoreRowUiModel(owls, 3, true)),
        )
        PartyGameScreen(state, onAction = {}, modifier = Modifier.fillMaxSize())
    }

    @Test
    fun playingGuessState() = capture {
        val state = PartyGameUiState.Playing(
            mode = PartyMode.GUESS_STATE,
            secondsLeft = 8,
            isTimeWarning = true,
            cardNumber = 3,
            cardCount = 7,
            cardKey = "0-2",
            cityName = "Springfield",
            stateCode = "MO",
        )
        PartyGameScreen(state, onAction = {}, modifier = Modifier.fillMaxSize())
    }

    @Test
    fun roundEnd() = capture {
        val state = PartyGameUiState.RoundEnd(
            team = owls,
            correct = 5,
            skipped = 2,
            pointsText = "+3",
            results = listOf(
                PartyResultRowUiModel("Austin", "TX", correct = true),
                PartyResultRowUiModel("Boise", "ID", correct = false),
            ),
            isLastTurn = false,
        )
        PartyGameScreen(state, onAction = {}, modifier = Modifier.fillMaxSize())
    }

    @Test
    fun gameEndTie() = capture {
        val state = PartyGameUiState.GameEnd(
            winner = null,
            standings = listOf(StandingRowUiModel(0, owls, 9), StandingRowUiModel(0, team1, 9)),
        )
        PartyGameScreen(state, onAction = {}, modifier = Modifier.fillMaxSize())
    }
}
