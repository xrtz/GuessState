package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.PartyCardResult
import ru.xrtz.whichstate.core.domain.model.PartyTeam
import javax.inject.Inject

class PartyScoring @Inject constructor() {
    fun points(correct: Boolean, penaltyOnSkip: Boolean): Int = when {
        correct -> 1
        penaltyOnSkip -> -1
        else -> 0
    }

    fun turnPoints(results: List<PartyCardResult>, penaltyOnSkip: Boolean): Int =
        results.sumOf { points(it.correct, penaltyOnSkip) }

    fun standings(teams: List<PartyTeam>): List<PartyTeam> = teams.sortedByDescending { it.score }

    fun isTie(teams: List<PartyTeam>): Boolean {
        val top = standings(teams)
        return top.size > 1 && top[0].score == top[1].score
    }
}
