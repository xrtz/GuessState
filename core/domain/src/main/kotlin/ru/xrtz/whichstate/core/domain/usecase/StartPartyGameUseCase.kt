package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.game.PartyGameState
import ru.xrtz.whichstate.core.domain.game.PartyRules
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.PartyTeam
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import javax.inject.Inject
import kotlin.random.Random

class StartPartyGameUseCase @Inject constructor(
    private val cityRepository: CityRepository,
    private val random: Random,
) {
    suspend operator fun invoke(
        mode: PartyMode,
        teamNames: List<String>,
        penaltyOnSkip: Boolean,
        rules: PartyRules = PartyRules(),
    ): PartyGameState {
        require(teamNames.size in rules.teamCounts) { "Unsupported team count: ${teamNames.size}" }
        val turnCount = teamNames.size * rules.totalRounds
        val needed = turnCount * rules.citiesPerTurn
        val pool = cityRepository.cities()
        require(pool.size >= needed) { "Need $needed cities, have ${pool.size}" }

        return PartyGameState(
            mode = mode,
            penaltyOnSkip = penaltyOnSkip,
            teams = teamNames.mapIndexed { index, name ->
                val customName = name.trim().take(rules.maxTeamNameLength).ifEmpty { null }
                PartyTeam(number = index + 1, customName = customName)
            },
            turns = pool.shuffled(random).take(needed).chunked(rules.citiesPerTurn),
            turnIndex = 0,
            phase = PartyGameState.Phase.Handoff,
            turnResults = emptyList(),
        )
    }
}
