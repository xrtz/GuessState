package ru.xrtz.whichstate.feature.menu

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
import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.usecase.GetCurrentLanguageUseCase
import ru.xrtz.whichstate.core.domain.usecase.GetLevelsUseCase
import ru.xrtz.whichstate.core.domain.usecase.ObserveMasteryUseCase
import ru.xrtz.whichstate.core.domain.usecase.ToggleLanguageUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeCityRepository
import ru.xrtz.whichstate.core.testing.fake.FakeLanguageRepository
import ru.xrtz.whichstate.core.testing.fake.FakeProgressRepository
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun city(name: String, state: UsState, minLevel: Level) =
        City(CityId("$name-${state.name}"), name, state, population = 100_000, minLevel = minLevel)

    private fun viewModel(
        cities: List<City> = emptyList(),
        mastered: Set<UsState> = emptySet(),
        languageRepository: FakeLanguageRepository = FakeLanguageRepository(),
    ) = MenuViewModel(
        GetLevelsUseCase(FakeCityRepository(cities)),
        ObserveMasteryUseCase(FakeProgressRepository(mastered)),
        GetCurrentLanguageUseCase(languageRepository),
        ToggleLanguageUseCase(languageRepository),
    )

    @Test
    fun `uiState carries per-level city counts from the domain`() = runTest {
        val viewModel = viewModel(
            cities = listOf(
                city("Springfield", UsState.MO, Level.ROOKIE),
                city("Columbus", UsState.OH, Level.EXPLORER),
            ),
        )

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        val state = viewModel.uiState.value

        assertEquals(3, state.levels.size)

        assertEquals(1, state.levels.single { it.level == Level.ROOKIE }.cityCount)
        assertEquals(2, state.levels.single { it.level == Level.EXPLORER }.cityCount)
        assertEquals("Rookie", state.levels.single { it.level == Level.ROOKIE }.displayName)
    }

    @Test
    fun `mastered count is null when nothing is mastered yet`() = runTest {
        val viewModel = viewModel(mastered = emptySet())

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.masteredCount)
    }

    @Test
    fun `mastered count reflects observed progress`() = runTest {
        val viewModel = viewModel(mastered = setOf(UsState.MO, UsState.TX))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.masteredCount)
    }

    @Test
    fun `selecting a level emits an OpenLevel effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effects.test {
            viewModel.onAction(MenuAction.SelectLevel(Level.EXPLORER))
            assertEquals(MenuEffect.OpenLevel(Level.EXPLORER), awaitItem())
        }
    }

    @Test
    fun `selecting a party mode emits an OpenParty effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effects.test {
            viewModel.onAction(MenuAction.SelectPartyMode(PartyMode.GUESS_STATE))
            assertEquals(MenuEffect.OpenParty(PartyMode.GUESS_STATE), awaitItem())
        }
    }

    @Test
    fun `opening history emits an OpenHistory effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effects.test {
            viewModel.onAction(MenuAction.OpenHistory)
            assertEquals(MenuEffect.OpenHistory, awaitItem())
        }
    }

    @Test
    fun `uiState starts with the repository's current language`() = runTest {
        val viewModel = viewModel(languageRepository = FakeLanguageRepository(AppLanguage.RUSSIAN))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(AppLanguage.RUSSIAN, viewModel.uiState.value.language)
    }

    @Test
    fun `toggling language flips it and updates uiState`() = runTest {
        val languageRepository = FakeLanguageRepository(AppLanguage.ENGLISH)
        val viewModel = viewModel(languageRepository = languageRepository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onAction(MenuAction.ToggleLanguage)
        advanceUntilIdle()

        assertEquals(AppLanguage.RUSSIAN, viewModel.uiState.value.language)
        assertEquals(AppLanguage.RUSSIAN, languageRepository.current())
    }
}
