# Which State? — архитектура

Android-приложение **Which State?** — порт веб-игры City Quiz (React + Vite, исходники: `C:\Users\thund\Downloads\city-quiz-v4\city-quiz`) на Kotlin + Jetpack Compose.
План утверждён 13.09.2026. Порядок работ — в [ROADMAP.md](ROADMAP.md).

---

## 1. Решения по продукту (v1)

| Тема | Решение | Относительно сайта |
|---|---|---|
| Название | Which State?; applicationId `ru.xrtz.whichstate` | новое |
| Магазин | Только RuStore | новое |
| Устройства | Телефоны, портретная ориентация, Android 10+ (minSdk 29) | как на сайте (PWA: portrait) |
| Сеть | Игра полностью офлайн; сеть нужна только рекламе | отправка в Supabase — позже |
| Язык | English по умолчанию + русский, переключение кнопкой в меню; все строки в ресурсах (`values`/`values-ru`) | расширено против сайта, см. §11.5 ROADMAP.md |
| Классика | Rookie 50 · Explorer 100 · Master 200, раунд 30 с | как на сайте |
| Party | Describe и Guess State с логикой сайта: 2–4 команды, 3 круга × 7 городов, 30 с, свайпы, штраф за пропуск | как на сайте |
| Фидбек | 500 мс после верного и неверного ответа; таймер идёт; тапы в это время игнорируются | как на сайте |
| Near-miss | Соседний штат — циан и «So close — it's the next state over!», в разборе «≈» | как на сайте |
| Результаты | Ранг, «верно / всего», перцентиль, строка позора, карта точности, плитки Correct / Wrong, «поделиться», N / 50, список городов, Play again / Change level | как на сайте |
| Ранги | 7 уровней по перцентилю × 10 шуточных вариантов с эмодзи; вариант = `correct % 10` | как на сайте |
| Перцентиль | Локальные таблицы по уровням (`percentile.js`) | как на сайте; настоящий — с сервером |
| Поделиться | PNG 1080×1920 через системное меню | как на сайте |
| Зум карты | Pinch-zoom 1–8× и сдвиг прямо в раунде | как на сайте |
| Одноимённые города | Верен любой штат, где в пуле есть город с таким названием (Springfield → MO или MA) | исправление |
| Список городов | Пересобрать по переписи 2020, починить сортировку, добавить в Master крупнейший город каждого штата без городов | исправление |
| «Освоено N / 50» | Штат освоен после ≥ 1 верного ответа, хранится локально | как на сайте; 50 / 50 теперь достижимо |
| Плитки Северо-Востока | 8 плиток: NH VT MA RI CT NJ DE MD, без DC | исправление |
| Сворачивание | Раунд встаёт на паузу | новое |
| Реклама | Yandex Mobile Ads: межстраничная при уходе с результатов (не после первого раунда, не чаще раза в 3 раунда) + баннер в меню и на результатах | новое |
| Аналитика и краш-репорты | После релиза, за интерфейсом (например, AppMetrica) | позже |
| Сервер | Supabase, настоящие перцентили, лидерборды, Battle Map с оплатой через RuStore | позже |

**Почему не AdMob:** Google с 2022 года не показывает рекламу пользователям в России, а приложение выходит только в RuStore.

**Правило по умолчанию:** если описание расходится с сайтом — делаем как на сайте. Явные баги сайта не копируем, а отмечаем как исправление.

### Что нашлось в коде сайта

- 205 городов; уровни — первые 50 / 100 / 200 записей списка. README устарел (обещает 150 и 500).
- Сортировка по населению нарушена в 10 местах — Explorer и Master не совсем «топ-N».
- Численности не из переписи 2020: у Нью-Йорка в данных 8 740 306, в переписи — 8 804 190.
- 9 штатов без единого города: DE, ME, MS, MT, NH, ND, VT, WV, WY.
- В Master 6 неразличимых пар (Aurora, Columbus, Glendale, Springfield, Kansas City, Pasadena): суффикс штата скрыт от игрока. «Columbia» записана дважды без суффикса.
- Плитка DC в сетке Северо-Востока: у DC нет городов, тап всегда ошибка (для MD/VA ещё и near-miss).
- `HIGHLIGHT_WRONG = 500` мс, хотя комментарий обещает 1400 мс. Переносим фактические 500 мс.
- Party-режимы не заглушки: логика целиком в `usePartyGame.js` и `PartyGame.jsx`.
- Перцентиль — статические таблицы. Результаты анонимно пишутся в Supabase; Battle Map — донаты через Stripe.

---

## 2. Принципы

1. **Правило зависимостей:** `feature → domain ← data`. `:app` — единственный composition root.
2. **Домен — чистый Kotlin (JVM-модуль).** Разрешены только stdlib, kotlinx-coroutines-core и `javax.inject`. Правила игры (таймер, проверка ответа, ранги, частота рекламы) живут здесь и тестируются без эмулятора.
3. **UDF в UI:** экран = `UiState` + `Action` + одноразовые `Effect`. ViewModel не знает о Compose, composable не знает о use case.
4. **Всё платформенное — за интерфейсами:** хранилище, реклама, шаринг, в будущем сеть и платежи. Замена реализации не трогает домен и экраны.
5. **Офлайн по умолчанию.** Сервер добавится новыми реализациями тех же интерфейсов.
6. **Границы проверяются автоматически:** Gradle-модули + Konsist-тесты + проверка в convention-плагине. Нарушение архитектуры — красная сборка.

---

## 3. Модули

```
:app ──────────────── composition root: DI, навигация, MainActivity
│
├── :feature:menu      ┐
├── :feature:game      │ presentation: Compose + ViewModel
├── :feature:results   │
├── :feature:party     ┘
├── :core:designsystem   Editorial Atlas
├── :core:map            рендер карты, hit-test, зум
├── :core:ads            API рекламы (интерфейсы)
│
├── :core:domain         чистый Kotlin: сущности, use cases, интерфейсы
│
├── :core:data           Room, DataStore, assets → реализации репозиториев
├── :core:ads-yandex     Yandex Mobile Ads → реализация :core:ads
└── :core:testing        фейки и фикстуры для тестов
```

| Модуль | Тип | Что внутри | Может зависеть от |
|---|---|---|---|
| `:app` | Android app | `Application`, `MainActivity`, граф навигации (Navigation 3), сборка Hilt | всех модулей |
| `:feature:menu` | Android lib + Compose | уровни, party-режимы, «N / 50», баннер | domain, designsystem, ads |
| `:feature:game` | Android lib + Compose | классический раунд | domain, designsystem, map |
| `:feature:results` | Android lib + Compose | итоги, карта точности, share-карточка, межстраничная реклама | domain, designsystem, map, ads |
| `:feature:party` | Android lib + Compose | setup → передача телефона → свайпы → итоги круга → итоги игры | domain, designsystem |
| `:core:domain` | Kotlin JVM | сущности, use cases, интерфейсы репозиториев, редьюсеры раундов, политики | — |
| `:core:data` | Android lib | assets JSON, Room, DataStore, реализации репозиториев, Hilt-биндинги | domain |
| `:core:designsystem` | Android lib + Compose | цвета, шрифты, типографика, компоненты | — |
| `:core:map` | Android lib + Compose | загрузка геометрии, `UsMap`, `AccuracyMap`, `NortheastTiles` | domain, designsystem |
| `:core:ads` | Android lib + Compose | `InterstitialAds`, `BannerAds`, `NoOpAds` | — |
| `:core:ads-yandex` | Android lib | реализация на Yandex Mobile Ads SDK | ads |
| `:core:testing` | Kotlin JVM | фейковые репозитории, тестовые часы, фикстуры | domain |
| `build-logic` | included build | convention-плагины | — |
| `tools/` | Node-скрипты | `map-builder`, `cities-builder` | — |

**Запрещено:** feature → data, feature → ads-yandex, feature → feature, domain → Android/Compose/SDK.
Фичи не знают друг о друге: переходы — лямбды (`onRoundFinished(roundId)`), граф собирает `:app`.
При выходе в Google Play появятся flavors `rustore` / `googleplay` и модуль `:core:ads-admob`.

### 3.1 Классы внутри модулей

Каждый слой работает со своими классами, а преобразование происходит ровно на границе слоя.

```
ЧТЕНИЕ (старт раунда)
cities.json ─decode→ CityDto ─toDomain()→ City → StartClassicRoundUseCase → GameViewModel ─toUiState()→ GameUiState → GameScreen
└────────── :core:data ──────────┘   └──────── :core:domain ────────┘   └──────────── :feature:game ────────────┘

ЗАПИСЬ (конец раунда)
GameViewModel ─RoundResult→ FinishClassicRoundUseCase → RoundHistoryRepository ⇢ RoundHistoryRepositoryImpl ─toEntity()→ RoundEntity → RoundDao (Room)
└ :feature:game ┘            └──────────────── :core:domain ────────────────┘   └───────────────── :core:data ─────────────────┘
```

| Тип класса | Суффикс | Где живёт | Пример |
|---|---|---|---|
| DTO (JSON ассетов, позже сети) | `Dto` | `:core:data` → `asset/dto`, `remote/dto` | `CityDto`, `CitiesFileDto` |
| Room-сущность | `Entity` | `:core:data` → `local/db/entity` | `RoundEntity`, `RoundAnswerEntity`, `StateProgressEntity` |
| DAO | `Dao` | `:core:data` → `local/db/dao` | `RoundDao`, `StateProgressDao` |
| Источник данных | `DataSource` | `:core:data` | `CitiesAssetDataSource`, позже `RoundsRemoteDataSource` |
| Маппер слоя данных | `toDomain()` / `toEntity()` | `:core:data` → `mapper` | `CityDto.toDomain()`, `RoundResult.toEntity()` |
| Реализация репозитория | `RepositoryImpl` | `:core:data` → `repository` | `RoundHistoryRepositoryImpl` |
| Доменная модель | без суффикса | `:core:domain` → `model` | `City`, `Question`, `RoundResult`, `RoundSummary` |
| Интерфейс репозитория | `Repository` | `:core:domain` → `repository` | `RoundHistoryRepository` |
| Use case | `UseCase` | `:core:domain` → `usecase` | `FinishClassicRoundUseCase` |
| Редьюсер, политика | `Reducer`, `Policy` | `:core:domain` → `game`, `policy` | `ClassicRoundReducer`, `RankPolicy` |
| ViewModel | `ViewModel` | `:feature:*` | `MenuViewModel`, `GameViewModel`, `ResultsViewModel`, `PartySetupViewModel`, `PartyGameViewModel` |
| Состояние экрана | `UiState` | `:feature:*` | `ResultsUiState` |
| UI-модель элемента | `UiModel` | `:feature:*` | `ResultRowUiModel`, `LevelCardUiModel` |
| Маппер в UI | `toUiState()`, `toUiModel()` | `:feature:*` | `RoundSummary.toUiState()` |
| Действия и эффекты | `Action`, `Effect` | `:feature:*` | `ResultsAction`, `ResultsEffect` |
| Экран | `Route` + `Screen` | `:feature:*` | `ResultsRoute`, `ResultsScreen` |

**Правила:**
- DTO и Entity не выходят за `:core:data`: репозиторий отдаёт наружу только доменные модели.
- Доменные модели — обычные data-классы без `@Serializable`, `@Entity` и других аннотаций фреймворков.
- ViewModel вызывает только use cases и никогда не обращается к репозиториям напрямую.
- `UiState` содержит готовые к показу данные; composable ничего не вычисляет.
- Мапперы — extension-функции в слое, который выполняет преобразование.

**Пакеты** (базовый пакет — от выбранного applicationId):

```
core/domain/      model/ · repository/ · usecase/ · game/ · policy/ · time/
core/data/        asset/ (CitiesAssetDataSource, dto/) · local/db/ (WhichStateDatabase, dao/, entity/) · local/prefs/ · mapper/ · repository/ · di/
feature/results/  ResultsRoute.kt · ResultsScreen.kt · ResultsViewModel.kt · ResultsUiState.kt · ResultsUiMapper.kt · share/ · navigation/ · components/
app/              WhichStateApp.kt · MainActivity.kt · navigation/ · di/
```

**Пример цепочки:**

```kotlin
// :core:data — asset/dto/CityDto.kt
@Serializable
internal data class CityDto(val id: String, val name: String, val state: String, val population: Int, val minLevel: Int)

// :core:data — mapper/CityMapper.kt
internal fun CityDto.toDomain() = City(CityId(id), name, UsState.valueOf(state), population, Level.entries[minLevel - 1])

// :core:data — repository/CityRepositoryImpl.kt
internal class CityRepositoryImpl @Inject constructor(
    private val assets: CitiesAssetDataSource,
) : CityRepository {
    override suspend fun cities(): List<City> = assets.readCities().map { it.toDomain() }
}

// :feature:results — ResultsViewModel.kt
@HiltViewModel(assistedFactory = ResultsViewModel.Factory::class)
class ResultsViewModel @AssistedInject constructor(
    @Assisted roundId: RoundId,
    getRoundSummary: GetRoundSummaryUseCase,
    private val shouldShowInterstitial: ShouldShowInterstitialUseCase,
) : ViewModel() {
    val uiState: StateFlow<ResultsUiState> = flow { emit(getRoundSummary(roundId).toUiState()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultsUiState.Loading)

    fun onAction(action: ResultsAction) { /* PlayAgain, ChangeLevel, Share */ }

    @AssistedFactory
    interface Factory { fun create(roundId: RoundId): ResultsViewModel }
}
```

---

## 4. Домен

### Сущности

```kotlin
enum class UsState(val fullName: String) { AL("Alabama"), AK("Alaska"), /* … */ WY("Wyoming") }

enum class Level { ROOKIE, EXPLORER, MASTER }

data class City(
    val id: CityId,
    val name: String,        // "Springfield" — без суффикса штата
    val state: UsState,
    val population: Int,
    val minLevel: Level,     // с какого уровня город попадает в пул
)

data class Question(val city: City, val acceptedStates: Set<UsState>)   // Springfield → {MO, MA}

enum class Verdict { CORRECT, NEAR_MISS, WRONG }                          // NEAR_MISS считается ошибкой

data class Answer(val question: Question, val picked: UsState, val verdict: Verdict, val atMs: Long)
```

Соседство штатов — таблица с сайта (`adjacency.js`) без DC, внутри `UsState`.

### Классический раунд — чистый редьюсер

```kotlin
data class RoundRules(
    val duration: Duration = 30.seconds,
    val feedbackCorrect: Duration = 500.milliseconds,
    val feedbackWrong: Duration = 500.milliseconds,
)

sealed interface RoundEvent {
    data class Tap(val state: UsState, val nowMs: Long) : RoundEvent
    data class Tick(val nowMs: Long) : RoundEvent       // ViewModel шлёт ~10 раз в секунду
    data class Pause(val nowMs: Long) : RoundEvent
    data class Resume(val nowMs: Long) : RoundEvent
}

class ClassicRoundReducer(private val judge: AnswerJudge, private val rules: RoundRules) {
    fun reduce(state: ClassicRoundState, event: RoundEvent): ClassicRoundState
}
```

Состояния: **Вопрос** → (тап) → **Фидбек** → (500 мс) → **Вопрос** … → **Финиш** (время 0 или пул пуст). Из любого состояния раунда — **Пауза** при сворачивании.
Оставшееся время считается от монотонного времени (`now − startedAt − pausedTotal`), а не декрементом по тикам: нет дрейфа, как у `setInterval` на сайте. ViewModel только подаёт `Tick(clock.now())` и тапы — все правила в редьюсере и тестируются без корутин.

### Политики (чистые функции)

| Политика | Правило |
|---|---|
| `AnswerJudge` | `picked ∈ acceptedStates` → CORRECT; сосед любого из них → NEAR_MISS; иначе WRONG |
| `PercentileTable` | таблицы сайта по уровням, ступенчато по числу верных |
| `RankPolicy` | пороги 0 · 12 · 28 · 48 · 65 · 80 · 100 перцентиля; отдаёт `Rank(tier, index = correct % 10)` — сам текст фразы не в домене, см. §11.5 ROADMAP.md |
| `WorstMissPolicy` | ошибка с самым крупным городом → строка позора |
| `AccuracyCalculator` | доля верных по штатам для карты точности |
| `AdFrequencyPolicy` | межстраничная: не после первого раунда сессии, не чаще раза в N = 3 раунда |
| `PartyScoring` | +1 за угаданный, −1 или 0 за пропуск (настройка штрафа) |

### Use cases

| Use case | Что делает |
|---|---|
| `GetLevelsUseCase` | уровни и число городов для карточек меню |
| `StartClassicRoundUseCase` | перемешанный пул уровня → `Question` с `acceptedStates` (`Random` инжектится) |
| `FinishClassicRoundUseCase` | атомарно сохраняет раунд и освоенные штаты → `RoundId` |
| `GetRoundSummaryUseCase` | ранг, перцентиль, строка позора, точность по штатам, N / 50 |
| `ObserveMasteryUseCase` | `Flow<MasteryProgress>` для меню и результатов |
| `StartPartyGameUseCase` | команды и пакеты по 7 городов без повторов на всю игру |
| `ShouldShowInterstitialUseCase` | решение о показе межстраничной рекламы |

### Интерфейсы репозиториев

```kotlin
interface CityRepository { suspend fun cities(): List<City> }

interface RoundHistoryRepository {
    suspend fun saveFinished(result: RoundResult): RoundId   // раунд + прогресс по штатам одной транзакцией
    suspend fun get(id: RoundId): RoundResult
}

interface ProgressRepository { fun observeMastered(): Flow<Set<UsState>> }

interface PercentileRepository { suspend fun percentile(level: Level, correct: Int): Int }

interface AdSessionRepository { fun roundsSinceInterstitial(): Int; fun onRoundFinished(); fun onInterstitialShown() }
```

---

## 5. Данные

- **`cities.json`** (assets `:core:data`) — генерирует `tools/cities-builder`: `id, name, state, population, rank, minLevel`. Пул уровня = города с `minLevel ≤ level`.
- **`us_states_albers.json`** (assets `:core:map`) — генерирует `tools/map-builder`.
- **Room** (`which_state.db`, схема экспортируется в репозиторий):
  - `rounds(id, mode, level, started_at, duration_ms, correct, wrong, synced_at)` — `synced_at` пригодится для отправки на сервер;
  - `round_answers(round_id, position, city_id, picked_state, verdict, at_ms)`;
  - `state_progress(state, correct_count, wrong_count, first_mastered_at)`.
- **DataStore** — флаги (первый запуск), позже настройки.
- Между экранами передаём `roundId`, а не объект: экран результатов переживает смерть процесса.

---

## 6. Карта

**Сборка (`tools/map-builder`, Node):** `us-atlas/states-10m.json` → `topojson-client` → `d3-geo` `geoAlbersUsa().fitSize(…)` — та же проекция, что у `react-simple-maps` на сайте → кольца полигонов, bbox и точка подписи для 50 штатов. Координаты округлены до 0,1; территории (PR, GU, VI, AS, MP) отброшены; DC — только декор. Скрипт рисует SVG-превью для сверки с сайтом.

**Устройство (`:core:map`):**
- геометрия парсится один раз (`Dispatchers.Default`) и кэшируется: `Path` для отрисовки, `FloatArray` для hit-test;
- отрисовка — один `Canvas` с `drawWithCache`; толщина штриха не зависит от зума;
- hit-test: обратное преобразование точки (вписывание + зум + сдвиг) → bbox → even-odd ray casting → если мимо, ближайший штат в радиусе 12 dp (тап по побережью);
- жесты: pinch-zoom 1–8× и сдвиг; тап засчитывается, только если палец не ушёл дальше touch slop и не было второго пальца;
- `AccuracyMap`: clip по контуру штата, сверху зелёная часть высотой «доля верных», снизу красная — как градиент на сайте;
- `NortheastTiles`: 8 плиток полосой под картой, цели касания ≥ 48 dp.

---

## 7. Экраны (presentation)

```kotlin
data class GameUiState(
    val cityName: String,
    val secondsLeft: Int,
    val isTimeWarning: Boolean,     // ≤ 10 с — таймер мигает красным
    val correct: Int,
    val wrong: Int,
    val highlight: MapHighlight?,   // выбранный, правильный, near-miss
    val isPaused: Boolean,
)

sealed interface GameAction {
    data class TapState(val state: UsState) : GameAction
    data object Pause : GameAction
    data object Resume : GameAction
    data object ExitToMenu : GameAction
}

sealed interface GameEffect {
    data class OpenResults(val roundId: RoundId) : GameEffect
}
```

- `GameRoute` получает ViewModel и собирает эффекты; `GameScreen(state, onAction)` без зависимостей — его рисуют превью и скриншот-тесты.
- Навигация — Navigation 3: ключи экранов объявлены в фичах, граф — в `:app`.
- DI — Hilt + KSP. Если с AGP 9 будут проблемы совместимости — Koin; архитектура от этого не меняется.
- Жизненный цикл: `ON_STOP` → `Pause`. Смерть процесса посреди 30-секундного раунда → возврат в меню.

---

## 8. Реклама

- `:core:ads` — `InterstitialAds { fun preload(); suspend fun show(activity): Boolean }`, `BannerAds { @Composable fun Banner(placement) }`, `NoOpAds` для тестов и отладки.
- `:core:ads-yandex` — реализация на Yandex Mobile Ads SDK. ID блоков — через `BuildConfig`, в debug — демо-блоки.
- Когда показывать, решает домен (`AdFrequencyPolicy`). Во время раунда и party-игры реклама не показывается никогда.
- Без сети реклама молча не грузится; баннер не занимает место, пока не загрузился.

---

## 9. Поделиться

`ShareCardRenderer` (`:feature:results`) рисует PNG 1080×1920 на `android.graphics.Canvas` по раскладке сайта: оверлайн, ранг, счёт, «BETTER THAN X% OF PLAYERS», строка позора, карта точности, легенда. Файл кладётся в `cacheDir` и уходит через `FileProvider` в `Intent.ACTION_SEND` с текстом на случай, если приложение не принимает картинки. Разрешения на хранилище не нужны.

---

## 10. Дизайн-система «Editorial Atlas»

| Токен | Значение |
|---|---|
| paper | `#F3F1EA` |
| card | `#FBFAF6` |
| ink | `#16203F` |
| muted | `#6A7290` |
| hairline | ink 13 % |
| cyan — акцент | `#0E9FC2` |
| red — ошибки и главные кнопки | `#D4374A` |
| green — верно | `#198F69` |
| map fill / stroke | `#E2DFD4` / ink 22 % |

- Шрифты вшиты в APK (`res/font`), без загрузки из сети: Unbounded 800 (заголовки, UPPERCASE, трекинг −0,02em), Inter 400–600 (текст), JetBrains Mono 400–500 (метки, коды штатов, трекинг 0,2–0,34em). Все три — с кириллицей (см. §11.5 ROADMAP.md).
- Скругление 4 dp, тени почти нулевые, hairline-границы 1 dp, нажатие — прозрачность 0,88 без ripple.
- Только светлая тема. Длинные названия («Rancho Cucamonga») ужимаются через auto-size текста.
- Компоненты: `AtlasButton` (primary / secondary / ink), `LevelCard`, `PartyModeCard`, `ScoreBadge`, `RoundTimer`, `StatTile`, `ResultRow`, `StateTile`, `Overline`, `Hairline`, `AtlasToggle`, `AtlasTextField`, `SwipeCard`.

---

## 11. Тесты и охрана архитектуры

- **Домен:** JUnit + kotlin.test; все сценарии сайта — двойной тап, конец времени во время фидбека, пул закончился, near-miss, одноимённые города, пауза. Покрытие домена ≥ 90 %.
- **Данные:** DAO и репозитории на Robolectric; тест ассетов — 50 штатов, сортировка, дубли.
- **Карта:** точка подписи каждого штата попадает в свой штат (unit); тап по всем 50 штатам (instrumented).
- **UI:** Compose-тест полного раунда на фейковых репозиториях; скриншот-тесты компонентов (Roborazzi).
- **Konsist:** в `core/domain` нет импортов `android.*` / `androidx.*`; ViewModel только в `feature/*`; `@Dao` / `@Entity` не выходят за `:core:data`; у use case один публичный `invoke`.
- **Convention-плагин** роняет сборку, если feature-модуль подключает `:core:data`, `:core:ads-yandex` или другую фичу.
- detekt, ktlint, Android Lint.

---

## 12. Стек

| Что | Чем |
|---|---|
| Сборка | Kotlin 2.2 · AGP 9.2 (встроенный Kotlin) · Gradle 9.4 · version catalog · convention-плагины |
| UI | Jetpack Compose (BOM 2026.02) · Material 3 как база под своей темой |
| Архитектура | ViewModel · StateFlow · Coroutines · Navigation 3 |
| DI | Hilt + KSP (запасной вариант — Koin) |
| Данные | Room · DataStore · kotlinx.serialization |
| Реклама | Yandex Mobile Ads SDK |
| Тесты | JUnit · kotlin.test · Turbine · coroutines-test · Robolectric · Roborazzi · Konsist |
| Качество | detekt · ktlint · Android Lint · LeakCanary (debug) |

Точные версии фиксируются в Фазе 0 после проверки совместимости с AGP 9.

---

## 13. Позже: сервер

- `:core:network` (Supabase, как на сайте, или свой backend), анонимная авторизация.
- Отправка раундов: `rounds.synced_at` + WorkManager (outbox).
- `RemotePercentileRepository` с локальными таблицами как запасным вариантом — настоящие перцентили.
- `:feature:leaderboard`, Battle Map с оплатой через платёжный SDK RuStore, мультиплеер.
- Домен и существующие экраны не переписываются — добавляются реализации интерфейсов.
