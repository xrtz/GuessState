package ru.xrtz.whichstate.feature.results

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
import ru.xrtz.whichstate.core.designsystem.components.ResultRowVariant
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h1400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ResultsScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun resultsScreenLoaded() {
        composeTestRule.setContent {
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
                        accuracyByState = mapOf(UsState.TX to 1f, UsState.MO to 0f, UsState.NY to 0.5f),
                        wrong = 16,
                        masteredCount = 12,
                        totalStates = 50,
                        cities = listOf(
                            ResultRowUiModel("Austin", "TX", ResultRowVariant.CORRECT),
                            ResultRowUiModel("Springfield", "MA → MO", ResultRowVariant.WRONG),
                            ResultRowUiModel("Kansas City", "KS → MO ~", ResultRowVariant.NEAR_MISS),
                        ),
                    ),
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun resultsScreenLoading() {
        composeTestRule.setContent {
            AtlasTheme {
                ResultsScreen(state = ResultsUiState.Loading, onAction = {}, modifier = Modifier.fillMaxSize())
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
