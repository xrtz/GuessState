package ru.xrtz.whichstate

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.feature.game.GameRoute
import ru.xrtz.whichstate.feature.history.HistoryRoute
import ru.xrtz.whichstate.feature.menu.MenuRoute
import ru.xrtz.whichstate.feature.party.game.PartyGameRoute
import ru.xrtz.whichstate.feature.party.setup.PartySetupRoute
import ru.xrtz.whichstate.feature.results.ResultsRoute
import java.util.UUID

private sealed interface Screen {
    data object Menu : Screen

    data class Round(val level: Level, val roundKey: String = UUID.randomUUID().toString()) : Screen

    data class Results(val roundId: RoundId) : Screen

    data class PartySetup(val mode: PartyMode, val setupKey: String = UUID.randomUUID().toString()) : Screen

    data class PartyGame(
        val mode: PartyMode,
        val teamNames: List<String>,
        val penaltyOnSkip: Boolean,
        val gameKey: String = UUID.randomUUID().toString(),
    ) : Screen

    data object History : Screen
}

private val ScreenSaver = Saver<Screen, String>(
    save = { screen ->
        when (screen) {
            is Screen.Results -> "results:${screen.roundId.value}"
            else -> "menu"
        }
    },
    restore = { encoded ->
        val parts = encoded.split(":")
        when (parts[0]) {
            "results" -> Screen.Results(RoundId(parts[1].toLong()))
            else -> Screen.Menu
        }
    },
)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtlasTheme { AppRoot() }
        }
    }
}

@Composable
private fun AppRoot() {
    var screen by rememberSaveable(stateSaver = ScreenSaver) { mutableStateOf<Screen>(Screen.Menu) }
    when (val current = screen) {
        Screen.Menu -> MenuRoute(
            onLevelSelected = { level -> screen = Screen.Round(level) },
            onPartyModeSelected = { mode -> screen = Screen.PartySetup(mode) },
            onHistorySelected = { screen = Screen.History },
            modifier = Modifier.fillMaxSize(),
        )
        is Screen.Round -> GameRoute(
            level = current.level,
            roundKey = current.roundKey,
            onExitToMenu = { screen = Screen.Menu },
            onRoundFinished = { roundId -> screen = Screen.Results(roundId) },
            modifier = Modifier.fillMaxSize(),
        )
        is Screen.Results -> ResultsRoute(
            roundId = current.roundId,
            onPlayAgain = { level -> screen = Screen.Round(level) },
            onChangeLevel = { screen = Screen.Menu },
            modifier = Modifier.fillMaxSize(),
        )
        is Screen.PartySetup -> PartySetupRoute(
            mode = current.mode,
            setupKey = current.setupKey,
            onStartGame = { mode, teamNames, penaltyOnSkip ->
                screen = Screen.PartyGame(mode, teamNames, penaltyOnSkip)
            },
            onBack = { screen = Screen.Menu },
            modifier = Modifier.fillMaxSize(),
        )
        is Screen.PartyGame -> PartyGameRoute(
            mode = current.mode,
            teamNames = current.teamNames,
            penaltyOnSkip = current.penaltyOnSkip,
            gameKey = current.gameKey,
            onExitToMenu = { screen = Screen.Menu },
            modifier = Modifier.fillMaxSize(),
        )
        Screen.History -> HistoryRoute(
            onBack = { screen = Screen.Menu },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
