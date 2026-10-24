package ru.xrtz.whichstate.feature.history

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
import ru.xrtz.whichstate.core.domain.model.Level

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h1400dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HistoryScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val loadedState = HistoryUiState.Loaded(
        selectedLevel = Level.ROOKIE,
        levels = listOf(
            LevelHistoryUiModel(
                level = Level.ROOKIE,
                displayName = "Rookie",
                best = AttemptUiModel(47, 50, 92, "Sep 10, 2026, 8:14 PM"),
                recent = listOf(
                    AttemptUiModel(47, 50, 92, "Sep 10, 2026, 8:14 PM"),
                    AttemptUiModel(32, 50, 61, "Sep 9, 2026, 6:02 PM"),
                ),
            ),
            LevelHistoryUiModel(Level.EXPLORER, "Explorer", best = null, recent = emptyList()),
            LevelHistoryUiModel(Level.MASTER, "Master", best = null, recent = emptyList()),
        ),
    )

    @Test
    fun historyScreenLoaded() {
        composeTestRule.setContent {
            AtlasTheme {
                HistoryScreen(state = loadedState, onAction = {}, modifier = Modifier.fillMaxSize())
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun historyScreenLoading() {
        composeTestRule.setContent {
            AtlasTheme {
                HistoryScreen(state = HistoryUiState.Loading, onAction = {}, modifier = Modifier.fillMaxSize())
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
