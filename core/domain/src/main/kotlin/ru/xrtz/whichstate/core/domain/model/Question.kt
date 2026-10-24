package ru.xrtz.whichstate.core.domain.model

data class Question(val city: City, val acceptedStates: Set<UsState>)
