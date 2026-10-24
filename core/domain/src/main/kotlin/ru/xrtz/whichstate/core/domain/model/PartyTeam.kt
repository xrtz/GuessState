package ru.xrtz.whichstate.core.domain.model

data class PartyTeam(val number: Int, val customName: String?, val score: Int = 0)

data class PartyCardResult(val city: City, val correct: Boolean)
