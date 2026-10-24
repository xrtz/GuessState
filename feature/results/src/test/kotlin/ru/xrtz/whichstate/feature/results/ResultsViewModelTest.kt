package ru.xrtz.whichstate.feature.results

import android.app.Activity
import android.net.Uri
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import ru.xrtz.whichstate.core.ads.InterstitialAds
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.policy.AccuracyCalculator
import ru.xrtz.whichstate.core.domain.policy.AdFrequencyPolicy
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.policy.RankPolicy
import ru.xrtz.whichstate.core.domain.policy.WorstMissPolicy
import ru.xrtz.whichstate.core.domain.usecase.GetRoundSummaryUseCase
import ru.xrtz.whichstate.core.domain.usecase.RecordInterstitialShownUseCase
import ru.xrtz.whichstate.core.domain.usecase.ShouldShowInterstitialUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeAdSessionRepository
import ru.xrtz.whichstate.core.testing.fake.FakeProgressRepository
import ru.xrtz.whichstate.core.testing.fake.FakeRoundHistoryRepository
import ru.xrtz.whichstate.feature.results.share.ShareCardRenderer
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

private const val EVERY_N_ROUNDS = 3

private class FakeShareCardRenderer(private val uri: Uri) : ShareCardRenderer {
    var renderCount = 0
        private set

    override suspend fun render(summary: RoundSummary): Uri {
        renderCount++
        return uri
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ResultsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val historyRepository = FakeRoundHistoryRepository()
    private val fakeUri = Uri.EMPTY
    private val activity: Activity = Robolectric.buildActivity(Activity::class.java).get()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun answer(name: String, state: UsState, picked: UsState, verdict: Verdict): Answer {
        val city = City(CityId(name), name, state, population = 100_000, minLevel = Level.ROOKIE)
        return Answer(Question(city, setOf(state)), picked = picked, verdict = verdict, atMs = 0)
    }

    private suspend fun givenFinishedRound(level: Level = Level.ROOKIE): RoundId = historyRepository.saveFinished(
        RoundResult(
            level = level,
            answers = listOf(answer("Austin", UsState.TX, UsState.TX, Verdict.CORRECT)),
            startedAtMs = 0,
            durationMs = 30_000,
        ),
    )

    private suspend fun viewModel(
        renderer: ShareCardRenderer = FakeShareCardRenderer(fakeUri),
        adSessionRepository: FakeAdSessionRepository = FakeAdSessionRepository(),
        interstitialAds: InterstitialAds = FakeInterstitialAds(),
    ): ResultsViewModel {
        val roundId = givenFinishedRound()
        val getRoundSummary = GetRoundSummaryUseCase(
            historyRepository,
            FakeProgressRepository(initialMastered = setOf(UsState.TX)),
            PercentileTable(),
            RankPolicy(),
            WorstMissPolicy(),
            AccuracyCalculator(),
        )
        val interstitialGate = ResultsInterstitialGate(
            ShouldShowInterstitialUseCase(adSessionRepository, AdFrequencyPolicy(EVERY_N_ROUNDS)),
            RecordInterstitialShownUseCase(adSessionRepository),
            interstitialAds,
        )
        return ResultsViewModel(roundId, getRoundSummary, renderer, interstitialGate)
    }

    @Test
    fun `uiState loads from Loading to Loaded`() = runTest {
        val viewModel = viewModel()

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = assertIs<ResultsUiState.Loaded>(viewModel.uiState.value)
        assertEquals(1, state.correct)
        assertEquals(1, state.total)
    }

    @Test
    fun `play again emits the round's own level`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onAction(ResultsAction.PlayAgain(activity))
            assertEquals(ResultsEffect.PlayAgain(Level.ROOKIE), awaitItem())
        }
    }

    @Test
    fun `change level emits a ChangeLevel effect`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onAction(ResultsAction.ChangeLevel(activity))
            assertEquals(ResultsEffect.ChangeLevel, awaitItem())
        }
    }

    @Test
    fun `share renders the card and clears isSharing once done`() = runTest {
        val renderer = FakeShareCardRenderer(fakeUri)
        val viewModel = viewModel(renderer)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onAction(ResultsAction.Share)
            assertEquals(ResultsEffect.ShareImage(fakeUri), awaitItem())
        }
        advanceUntilIdle()

        assertEquals(1, renderer.renderCount)
        val state = assertIs<ResultsUiState.Loaded>(viewModel.uiState.value)
        assertFalse(state.isSharing)
    }

    @Test
    fun `an interstitial due on load is preloaded, then shown and recorded on exit`() = runTest {
        val ads = FakeInterstitialAds(showResult = true)
        val adSession = FakeAdSessionRepository(initialRounds = EVERY_N_ROUNDS)
        val viewModel = viewModel(adSessionRepository = adSession, interstitialAds = ads)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(1, ads.preloadCount)

        viewModel.effects.test {
            viewModel.onAction(ResultsAction.ChangeLevel(activity))
            assertEquals(ResultsEffect.ChangeLevel, awaitItem())
        }
        advanceUntilIdle()

        assertEquals(1, ads.showCount)
        assertEquals(1, adSession.interstitialShownCount)
    }
}
