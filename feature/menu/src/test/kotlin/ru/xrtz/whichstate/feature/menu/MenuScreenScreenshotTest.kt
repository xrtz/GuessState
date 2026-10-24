package ru.xrtz.whichstate.feature.menu

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
class MenuScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menuScreenWithMasteredStates() {
        composeTestRule.setContent {
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
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun menuScreenWithNoMasteredStates() {
        composeTestRule.setContent {
            AtlasTheme {
                MenuScreen(
                    state = MenuUiState(
                        levels = listOf(
                            LevelCardUiModel(Level.ROOKIE, "Rookie", 50, 1),
                            LevelCardUiModel(Level.EXPLORER, "Explorer", 100, 2),
                            LevelCardUiModel(Level.MASTER, "Master", 200, 3),
                        ),
                        masteredCount = null,
                        totalStates = 50,
                    ),
                    onAction = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Config(sdk = [35], qualifiers = "w800dp-h400dp")
    @Test
    fun menuScreenLandscape() {
        composeTestRule.setContent {
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
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
