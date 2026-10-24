package ru.xrtz.whichstate.core.domain.model

data class MasteryProgress(val mastered: Set<UsState>, val total: Int = TOTAL_STATES) {
    companion object {
        const val TOTAL_STATES = 50
    }
}
