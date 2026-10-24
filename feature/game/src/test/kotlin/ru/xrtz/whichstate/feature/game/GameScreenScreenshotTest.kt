package ru.xrtz.whichstate.feature.game

import androidx.compose.foundation.layout.fillMaxSize
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
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.map.MapHighlight

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GameScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun askingForACity() {
        composeTestRule.setContent {
            AtlasTheme {
                GameScreen(
                    state = GameUiState(cityName = "Springfield", secondsLeft = 24, correct = 3, wrong = 1),
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Config(sdk = [35], qualifiers = "w800dp-h400dp")
    @Test
    fun askingForACityLandscape() {
        composeTestRule.setContent {
            AtlasTheme {
                GameScreen(
                    state = GameUiState(cityName = "Rancho Cucamonga", secondsLeft = 24, correct = 3, wrong = 1),
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun nearMissFeedback() {
        composeTestRule.setContent {
            AtlasTheme {
                GameScreen(
                    state = GameUiState(
                        cityName = "Springfield",
                        secondsLeft = 7,
                        correct = 3,
                        wrong = 2,
                        highlight = MapHighlight(UsState.MA, UsState.MO, Verdict.NEAR_MISS),
                        isNearMiss = true,
                    ),
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
