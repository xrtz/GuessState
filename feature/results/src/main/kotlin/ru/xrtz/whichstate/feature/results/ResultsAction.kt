package ru.xrtz.whichstate.feature.results

import android.app.Activity

sealed interface ResultsAction {
    data class PlayAgain(val activity: Activity) : ResultsAction
    data class ChangeLevel(val activity: Activity) : ResultsAction
    data object Share : ResultsAction
}
