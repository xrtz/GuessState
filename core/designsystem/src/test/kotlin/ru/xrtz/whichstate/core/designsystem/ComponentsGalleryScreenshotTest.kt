package ru.xrtz.whichstate.core.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
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
import ru.xrtz.whichstate.core.designsystem.components.ComponentsGallery
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h1800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ComponentsGalleryScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun componentsGallery() {
        composeTestRule.setContent {
            AtlasTheme { ComponentsGallery(modifier = Modifier.fillMaxWidth()) }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
