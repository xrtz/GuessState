package ru.xrtz.whichstate.feature.results

import android.net.Uri
import ru.xrtz.whichstate.core.domain.model.Level

sealed interface ResultsEffect {
    data class PlayAgain(val level: Level) : ResultsEffect
    data object ChangeLevel : ResultsEffect

    data class ShareImage(val uri: Uri) : ResultsEffect
}
