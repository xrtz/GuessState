package ru.xrtz.whichstate.core.domain.model

data class Answer(val question: Question, val picked: UsState, val verdict: Verdict, val atMs: Long)
