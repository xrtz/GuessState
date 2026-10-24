# Which State? — карта разработки

Фазы идут по порядку: каждая опирается на предыдущие. Фазы 00–11 (и внеплановая 11.5) — первая версия до публикации в RuStore.
Архитектура и решения — в [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 00 · Фундамент

Пустое приложение с правильной модульной структурой и автоматической охраной архитектуры.

- [x] `git init`, первый коммит
- [x] Название приложения: Which State? (подпись в `strings.xml` уже заменена)
- [x] applicationId и namespace `ru.xrtz.whichstate` вместо `com.example.guessstate` (после первой публикации не меняется)
- [x] `build-logic`: convention-плагины `jvm-library`, `android-library`, `android-compose`, `android-feature`, `hilt`
- [x] Модули из ARCHITECTURE.md §3
- [x] Hilt, Coroutines, kotlinx.serialization, Room, DataStore, Navigation 3, Konsist, detekt, ktlint; версии зафиксированы, совместимость с AGP 9 проверена (KSP пришлось поднять до 2.3.11 — AGP 9 built-in Kotlin ломался на связке Kotlin 2.2.10 + старый KSP; detekt-formatting/ktlint отключены из-за NPE в ktlint-интеграции detekt 1.23.8, см. config/detekt/detekt.yml)
- [x] Проверка запрещённых зависимостей в convention-плагине
- [x] Обновить CLAUDE.md под реальные модули и команды

**Готово, когда:** `gradlew assembleDebug check` зелёный, Konsist-тесты проходят, приложение открывает пустой экран цвета paper.

## 01 · Данные: города и карта

Проверенные ассеты вместо ручного `cities.js` и сетевого TopoJSON.

- [x] `tools/cities-builder`: официальные данные переписи 2020 (incorporated places) → топ-200 + крупнейший город каждого штата, которого нет в топ-200 → `cities.json` с `rank` и `minLevel` (Census API требует ключ регистрации — данные из wikitext-таблицы Википедии, 2020 census; 208 городов = топ-200 + 8 штатов: DE, ME, MT, NH, ND, VT, WV, WY)
- [x] Проверки: все 50 штатов в Master, сортировка, дубли (name, state), отчёт по одноимённым городам (`tools/cities-builder/verify.mjs`, `duplicate-names-report.json`)
- [x] `tools/map-builder`: TopoJSON → Albers USA → `us_states_albers.json` + SVG-превью
- [x] Таблица соседних штатов (без DC) — `tools/map-builder/adjacency.json`, симметрия проверена; Kotlin-перенос в `UsState` — задача Фазы 02

**Готово, когда:** ассеты лежат в `:core:data` и `:core:map`, проверки проходят, превью карты совпадает с сайтом.

## 02 · Домен

Все правила игры в чистом Kotlin, написаны через тесты.

- [x] Сущности: `UsState` с соседями, `City`, `Level`, `Question`, `Answer`, `RoundResult` (плюс `RoundSummary`, `MasteryProgress`, `LevelInfo` для use cases)
- [x] `AnswerJudge`: верно / near-miss / ошибка, одноимённые города
- [x] `ClassicRoundReducer`: 30 с по монотонному времени, фидбек 500 / 500 мс, игнор тапов во время фидбека, конец пула, пауза
- [x] `PercentileTable`, `RankPolicy` (таблицы и 70 фраз с сайта, сверены построчно скриптом против percentile.js), `WorstMissPolicy`, `AccuracyCalculator`, `AdFrequencyPolicy`
- [x] Интерфейсы репозиториев и use cases (Party-режим — не сюда, это Фаза 09)

**Готово, когда:** все сценарии сайта покрыты тестами, покрытие домена ≥ 90 %.

## 03 · Слой данных

Реализации репозиториев поверх ассетов, Room и DataStore.

- [x] Чтение ассетов (kotlinx.serialization) с кэшем в памяти (`CitiesAssetDataSource`, mutex-guarded)
- [x] Room: `rounds`, `round_answers`, `state_progress`; экспорт схемы (`core/data/schemas/`)
- [x] DataStore для флагов (`AppPreferencesDataSource` — инфраструктура вперёд, ещё не используется)
- [x] Реализации репозиториев и Hilt-модули (`:core:data` теперь применяет `whichstate.hilt`)

**Готово, когда:** раунд сохраняется и читается по `roundId`, освоенные штаты считаются верно, тесты на Robolectric зелёные.

## 04 · Дизайн-система

Editorial Atlas в Compose: токены, шрифты, компоненты.

- [x] Шрифты в `res/font` + тексты лицензий OFL
- [x] Токены цвета, типографики, форм; `AtlasTheme`
- [x] Компоненты из ARCHITECTURE.md §10 с превью
- [x] Edge-to-edge, splash-экран цвета paper
- [x] Скриншот-тесты (Roborazzi)

**Готово, когда:** debug-галерея компонентов визуально совпадает со скриншотами сайта.

## 05 · Карта

Тапабельная векторная карта без сети.

- [x] Загрузка геометрии, кэш `Path`
- [x] Отрисовка и анимация подсветки: верно / ошибка + правильный / near-miss
- [x] Hit-test с допуском 12 dp
- [x] Pinch-zoom и сдвиг, разделение тапа и жеста
- [x] `AccuracyMap`, `NortheastTiles`
- [ ] Instrumented-тест на 50 штатов + проверка на реальном/эмулированном устройстве — нет
      эмулятора в этом окружении, см. заметку ниже

**Готово, когда:** тап по каждому из 50 штатов даёт нужный код (instrumented-тест), зум не вызывает случайных тапов, нет пропуска кадров на среднем телефоне.

**Заметка:** реализация и hit-test математика (point-in-polygon, включая реальные мультиполигоны
с "дырками" — формат `rings` у tools/map-builder на деле GeoJSON-подобный, 4 уровня вложенности,
не 3) покрыты unit-тестами на синтетической и реальной геометрии (`StateHitTesterTest`,
`MapAssetDataSourceTest`, включая проверку «точка подписи внутри своего штата» для всех штатов
кроме HI — у Гавайев геометрический центроид архипелага объективно лежит в воде между островами,
это не баг). Тап/зум-жесты и `Canvas`-рендер не проверены интерактивно — в этом окружении нет
adb/эмулятора. Перед Фазой 07 (когда `UsMap` попадёт в реальный экран раунда) стоит один раз
руками проверить жесты и завести `connectedAndroidTest` на 50 штатов.

## 06 · Меню

- [x] Оверлайн, заголовок WHICH STATE?, слоган
- [x] Карточки уровней 01–03: точки сложности, число городов, «30 sec»
- [x] Блок Party modes
- [x] «N / 50 states mastered» (скрыт при 0, как на сайте)
- [x] `MenuViewModel` шлёт `MenuEffect.OpenLevel`/`OpenParty` — сама навигация ждёт Фазы 07/09

**Готово, когда:** из меню запускается раунд выбранного уровня, счётчик обновляется сам.

**Заметка:** `MenuScreen`/`MenuViewModel`/`MenuRoute` реализованы и покрыты тестами (ViewModel —
per-level счётчики городов и реактивность «N / 50» на фейковых репозиториях из `:core:testing`;
Roborazzi-скриншот экрана в обоих состояниях — со счётчиком и без). `MainActivity` уже показывает
`MenuRoute` вместо заглушки. Но «запускается раунд» в буквальном смысле недостижимо: `:feature:game`
и `:feature:party` ещё не существуют, поэтому колбэки `onLevelSelected`/`onPartyModeSelected` в
`MainActivity` — no-op с `TODO` до соответствующих фаз.

## 07 · Классический раунд

- [x] Шапка: ← Menu, ✓ / ✗, таймер (мигает красным при ≤ 10 с)
- [x] «Which state is this city in?», смена названия с анимацией, строка near-miss
- [x] Карта + плитки Северо-Востока, фидбек, автопереход
- [x] Пауза при сворачивании (`ON_STOP`/`ON_START`); системная «назад» = выход в меню, как кнопка Menu
- [x] Финиш → `FinishClassicRoundUseCase` → `GameEffect.OpenResults(roundId)`

**Готово, когда:** раунд целиком играется офлайн; UI-тест «сыграть и дойти до результатов» зелёный.

**Заметка:** `GameViewModel`/`GameScreen`/`GameRoute` реализованы; `GameViewModel` — тестами
(старт раунда, верный тап → highlight → авто-переход к следующему городу по истечении фидбека,
конец времени → `FinishClassicRoundUseCase` → `OpenResults` с реальным `roundId`, выход в меню).
Два реальных бага поймали именно эти тесты, не ручная проверка: `uiState`/`SharingStarted.
WhileSubscribed` не обновлялся без активного подписчика (тот же баг, что в Phase 06 —
`.value` без `.collect` замораживается на seed-значении), и бесконечный тикер
(`while(true) { delay(100) }` в `viewModelScope`) вешал `runTest` намертво, потому что тест-раннер
досуха вычерпывает общий с `Dispatchers.Main` шедулер при выходе из `runTest{}` — обе фазы это
не встречали, потому что ни в одном ViewModel раньше не было бесконечного цикла. `GameScreen`
также проверен Roborazzi-скриншотами (обычный вопрос и near-miss фидбек) — карта, подсветка,
NE-плитки и текст рендерятся корректно.

Не сделано / не проверено: инструментированного UI-теста «сыграть весь раунд до результатов»
нет — только ViewModel-тесты на Robolectric/JVM; пауза при сворачивании и системная «назад»
не проверены руками на устройстве (нет эмулятора в этом окружении, как и в Фазе 05); переход
на результаты — заглушка (`onRoundFinished` в `MainActivity` просто возвращает в меню), потому
что `:feature:results` ещё не существует (Фаза 08).

## 08 · Результаты и «поделиться»

- [x] «Time's up!», ранг, «верно / всего», перцентиль, строка позора
- [x] Карта точности с легендой, плитки Correct / Wrong, «N / 50»
- [x] Список «Cities answered — N»: ✓ / ✗ / ≈, «выбрал → правильно»
- [x] Нижняя панель Play again / Change level
- [x] Share-карточка 1080×1920 + `FileProvider` + системное меню

**Готово, когда:** экран переживает смерть процесса, картинка отправляется в Telegram и VK на Android 10–16.

**Заметка:** `ResultsScreen`/`ResultsViewModel`/`ResultsRoute` реализованы и подключены в
`MainActivity` (`Screen.Results(roundId)` после `GameEffect.OpenResults`); `ResultsUiMapper` и
`ResultsViewModel` покрыты unit-тестами (переходы Loading → Loaded, PlayAgain/ChangeLevel-эффекты,
Share → рендер + сброс `isSharing`), `ResultsScreen` — Roborazzi-скриншотами (загруженное и
Loading-состояния). `RoundSummary`/`GetRoundSummaryUseCase` расширены `answers`/`totalStates` для
списка городов и «N / 50»; `PercentileTable`/`RankPolicy`/`WorstMissPolicy`/`AccuracyCalculator`
получили `@Inject constructor()`, чтобы Hilt собирал `GetRoundSummaryUseCase` без ручного модуля.
Share-карточка (`ShareCardRenderer`/`AndroidShareCardRenderer`, `:feature:results/share`) рисует
PNG 1080×1920 через `android.graphics.Canvas` — те же пиксельные координаты, что в
`Results.jsx#handleShare`, только оверлайн/футер переименованы под Which State? — и переиспользует
`:core:map`'s геометрию и `AtlasColors`; за `FileProvider` (`androidx.core.content.FileProvider`,
authority `${applicationId}.fileprovider`) отвечает манифест `:app` (`res/xml/file_paths.xml`).
`ShareCardRenderer` — интерфейс с `AndroidShareCardRenderer`-реализацией и Hilt-биндингом
(`ResultsModule`) специально для тестируемости `ResultsViewModel` без реального `Context`.
`ResultsRoute` ключует `hiltViewModel()` по `roundId.value` — как и `GameRoute` с `roundKey`
(Фаза 07), экран-свитч в `MainActivity` не настоящая навигация, так что без явного ключа
Activity-scoped `ViewModelStore` отдавал бы старый экземпляр при повторных заходах.
`screen` в `MainActivity` теперь `rememberSaveable` с ручным `Saver` (`ScreenSaver`): `Results`
восстанавливается по сохранённому `roundId` после смерти процесса, а `Round` — сознательно нет
(разворачивается в `Menu`), потому что у него нет спасённого состояния раунда для возобновления
и это ровно то поведение, которое уже описано в §7 («смерть процесса посреди 30-секундного раунда
→ возврат в меню»).

Не проверено на устройстве (нет эмулятора в этом окружении, как и в Фазах 05/07): реальная
отправка PNG в Telegram/VK через `Intent.ACTION_SEND`, читаемость текста на share-карточке с
реальными вариативными шрифтами (`Typeface.create(family, weight, italic)` резолвит ближайший
инстанс, а не гарантированно ровно 800/500), и собственно восстановление `Screen.Results` через
`rememberSaveable` при настоящем kill процесса (а не просто пересоздании Activity в тесте).

## 09 · Party-режимы

- [x] Домен: `PartyGameReducer` — порт `usePartyGame` с тестами
- [x] Setup: 2–4 команды, имена до 20 символов, штраф за пропуск
- [x] Handoff: «Round X of 3», «Pass the phone», мини-таблица очков
- [x] Playing: таймер 30 с, карточка со свайпом (порог 60 dp) и кнопки Skip / Correct, экран не гаснет
- [x] Round end и Game end: сортировка, медали, Play again / Menu

**Готово, когда:** 2–4 команды проходят 3 круга, очки совпадают с сайтом со штрафом и без.

**Заметка:** домен — `PartyGameReducer` (монотонное время и пауза как у классики), `PartyScoring`,
`StartPartyGameUseCase` (пул — все города, пакеты по 7 без повторов), покрыт тестами; фича —
`PartySetupViewModel`/`PartyGameViewModel` с тестами и Roborazzi-скриншоты всех пяти экранов.
Сворачивание ставит ход на паузу. Исправления относительно сайта: команды идентифицируются
номером, а не именем (одинаковые имена ломали подсветку и счёт); ничья за первое место —
«It's a tie!», равные очки делят место и медаль (сайт объявлял победителем первую); ответ
засчитывается в момент свайпа/нажатия, а не после 280 мс анимации (на сайте ответ в последние
280 мс хода терялся). Поля имён пустые с плейсхолдером «Team N» вместо предзаполненных
«Team 1/2» — пустое имя всё равно показывается как «Team N».

Попутно найден и исправлен баг Фазы 07: `GameViewModel` клал в `StateFlow` тот же объект
состояния на каждом тике, `StateFlow` отбрасывал дубликат, и таймер на экране обновлялся только
после тапа. Провайдеры `GameClock`/`Random` переехали из `:feature:game` в `:app`
(`TimeAndRandomModule`), чтобы `:feature:party` не зависела от биндинга соседней фичи.

Не проверено на устройстве: реальный свайп пальцем и анимация улетающей карточки, `keepScreenOn`.

## 10 · Реклама

- [x] `:core:ads` и `:core:ads-yandex`, инициализация вне главного потока
- [x] Баннер в меню и на результатах
- [x] Межстраничная при уходе с результатов по `AdFrequencyPolicy`
- [x] Демо-блоки в debug, боевые ID в release

**Готово, когда:** реклама не появляется во время игры, частота соблюдается, без сети приложение работает полностью.

**Заметка:** `:core:ads` — интерфейсы `InterstitialAds`/`BannerAds`/`AdPlacement`/`NoOpAds`, без
Android-зависимостей кроме Activity/Compose (депендится ноль модулей, как в §3). `:core:ads-yandex`
— `com.yandex.android:mobileads:8.4.0` (Maven Central, доп. репозиторий не понадобился);
`YandexAdsInitializer` вызывает `YandexAds.initialize` один раз на `Dispatchers.Default`, все
остальные вызовы ждут его через `CompletableDeferred`. `AndroidInterstitialAds` грузит через
`InterstitialAdLoader`/`InterstitialAdLoadListener` и показывает через `InterstitialAdEventListener`,
обёрнутые в `suspendCancellableCoroutine`; `AndroidBannerAds` — `BannerAdView` в `AndroidView`,
занимает 0dp, пока не пришёл `onAdLoaded()` (по решению §8). Точные имена классов/методов SDK 8
(`YandexAds`, `InterstitialAdLoader`, `BannerAdView`, `AdRequest.Builder` и т.д.) не гадал по
документации — скачал `mobileads-8.4.0.aar` и прогнал `javap` по `classes.jar`, потом собрал
`:core:ads-yandex` в debug и release как проверку. Демо-блоки (`demo-banner-yandex`,
`demo-interstitial-yandex` — официальные тестовые ID Яндекса) прописаны в
`buildConfigField` для обоих build type; боевые ID — `TODO(Phase 11)` в
`core/ads-yandex/build.gradle.kts`, ждут аккаунта в Рекламной сети Яндекса.

`FinishClassicRoundUseCase` теперь считает раунд в счётчик `AdFrequencyPolicy` (раньше никто не
дёргал `AdSessionRepository.onRoundFinished()`). `ResultsInterstitialGate` (`:feature:results`) —
отдельный класс на `ShouldShowInterstitialUseCase` + `RecordInterstitialShownUseCase` +
`InterstitialAds`, вынесен из `ResultsViewModel` только чтобы не разрастался конструктор
(detekt `LongParameterList`), но заодно тестируется отдельно от ViewModel. Решение показывать
рекламу принимается один раз при загрузке экрана результатов (там же и `preload()`), а не заново
при каждом нажатии Play again/Change level — межстраничная не завязана на party-режимы, эта фаза
их не трогает (в §1 реклама для party не описана).

Не проверено на устройстве (нет эмулятора в этом окружении): сама реклама на экране — показ,
загрузка баннера, `keepScreenOn` во время interstitial и т.д. Собранное debug-приложение ещё не
запускалось на телефоне/эмуляторе ни разу за всю разработку — стоит сделать это перед Фазой 11.

## 11 · Полировка и релиз в RuStore

- [x] Адаптивная иконка (из иконок сайта), название, splash
- [x] Доступность: подписи для TalkBack, крупный шрифт, цели касания ≥ 48 dp
- [x] R8 и правила ProGuard (serialization, Yandex Ads); Baseline Profile — не сделано, см. заметку; проверка размера
- [ ] Политика конфиденциальности: рекламный SDK использует рекламный идентификатор
- [ ] Аккаунт разработчика RuStore и аккаунт в Рекламной сети Яндекса
- [ ] Карточка: описание, скриншоты, возрастной рейтинг, отметка о рекламе; подписанная релизная сборка; модерация

**Готово, когда:** приложение опубликовано в RuStore.

**Заметка:** три пункта, которые не требуют аккаунтов, сделаны в один заход.

*Иконка.* Сайт использует монограмму «CQ» (Bricolage Grotesque, ink-on-paper карточка со
скруглением, cyan-подчёркивание, тёмно-синий фон) — с переименованием в Which State? буквы
стали «WS», сама формула не менялась. Рисовал не вручную путями, а Python + Pillow прямо
настоящим шрифтом `core/designsystem/src/main/res/font/bricolage_grotesque.ttf` (у него уже
дефолтный инстанс — вес 800, как и `AtlasType.display`), сгенерировал foreground/background
и легаси mipmap на всех плотностях, проверил на круглой и squircle-масках, что карточка не
обрезается. Скрипт лежал во временном scratchpad, в репозиторий не попал — при необходимости
переделать иконку проще заново прогнать похожий скрипт, чем держать генератор в дереве.

*Доступность.* Карта (`UsMap`) была совсем недоступна для TalkBack — Canvas без семантики,
50 штатов нельзя было ни найти, ни тапнуть вслепую. Добавлены `customActions` (по одному на
`UsState`, каждый с `fullName` вместо кода) — TalkBack показывает их как список для
исследования жестом, независимо от pinch/tap. Плитки Северо-Востока (`StateTile`) объявляли
48 dp в архитектуре, а по факту были 44×36 — поджато через `minimumInteractiveComponentSize()`
(визуально ничего не изменилось, тачтаргет вырос). Тем же способом подтянуты `AtlasButton`
(им кнопки Play again/Change level/Start game/Share и т.д. пользуются почти everywhere),
`AtlasToggle`, кнопки «← Menu»/«← Back», плитки числа команд и Skip/Correct в party. Заодно
`AtlasToggle` и «сколько команд» переведены с голого `clickable` на `toggleable`/`selectable`
с `Role.Switch`/`Role.RadioButton` — TalkBack теперь называет их переключателем/радиокнопкой
и читает состояние, а не просто «дважды нажмите для активации». Крупный шрифт — на `sp`
без разработческих трюков, отдельно на устройстве при 200% масштабе не проверял (негде).
Полный прогон `check` после этих правок остался зелёным, ни один Roborazzi-скриншот не
изменился — визуально ничего не сдвинулось.

*R8/ProGuard.* `isMinifyEnabled = true` и `isShrinkResources = true` для release. Правила
kotlinx.serialization — дословно из README библиотеки (`-keep,includedescriptorclasses
class ru.xrtz.whichstate.**$$serializer`), Yandex Ads несёт свой `proguard.txt` внутри aar
и подключается сам. Без эмулятора нельзя прогнать релизную сборку и проверить, что JSON
по-прежнему decode'ится — но `app/build/outputs/mapping/release/mapping.txt` после сборки
показывает, что `CityDto$$serializer`/`MapAssetJson` пережили shrink с сохранённой структурой
`serialize`/`deserialize`, а `resources.txt` подтверждает, что иконка дошла до APK (reachable
от `AndroidManifest.xml`) — это настоящая проверка по артефактам сборки, не просто «должно
сработать по документации». Release APK — 9.3 МБ (debug для сравнения — 47 МБ).

Baseline Profile не делал: он собирается `BaselineProfileRule`-тестом, который реально
запускает приложение на устройстве/эмуляторе и записывает профиль — без эмулятора в этом
окружении получится только пустая заглушка, которая хуже, чем ничего. Сделать отдельно, когда
появится возможность погонять сборку руками (там же — первый шанс вообще увидеть приложение
живьём, а не только зелёным `check`).

## 11.5 · Локализация (EN/RU)

Внеплановая фаза: не входила в исходный v1-список, добавлена по ходу разработки вместо
пункта «Русский интерфейс» из «Идей на потом».

- [x] Шрифты, поддерживающие кириллицу: Bricolage Grotesque → Unbounded (display), Archivo →
      Inter (body); JetBrains Mono (mono) кириллицу уже поддерживал
- [x] Домен: `AppLanguage`, `LanguageRepository`, `GetCurrentLanguageUseCase`,
      `ToggleLanguageUseCase`; `AndroidLanguageRepository` поверх `AppCompatDelegate.
      setApplicationLocales` (`:core:data`)
- [x] Квадратная кнопка EN/RU сверху справа на главном экране, общий дизайн не нарушен
- [x] Все строки экранов переведены (`values-ru` в каждом feature-модуле и `:core:map`),
      включая 70 фраз ранга результатов и текст share-карточки

**Готово, когда:** переключение языка сразу меняет весь интерфейс без ручного перезапуска
приложения, кириллица нигде не показывает «тофу»-квадраты вместо букв.

**Заметка:** прежде чем менять шрифты, кириллица (и её отсутствие у Bricolage Grotesque/Archivo)
проверена не на глаз, а рендером через Python + Pillow — каждый кандидат прогонялся через
`PIL.ImageFont` на кириллическую и латинскую тестовую строку, PNG смотрелся глазами на предмет
`.notdef`-квадратов. Unbounded и Inter (оба — Google Fonts, `google/fonts` репозиторий на GitHub,
скачаны напрямую `curl`) прошли проверку на латиницу и кириллицу. Один шрифт на роль вместо
вилки «свой шрифт под каждый язык» — сознательное решение: не было явного запроса на разный
рендеринг, а один голос бренда лучше держит «общий дизайн», о котором просили при формулировке
задачи.

`AppCompatDelegate.setApplicationLocales()` ниже API 33 работает только через `AppCompatActivity`
(бэкпорт `LocaleManager` держится на хуках жизненного цикла именно этого класса) — проверено
через WebSearch, а не по памяти, потому что ошибиться здесь значит переписывать `MainActivity` и
`themes.xml` заново. `MainActivity` перешла с `ComponentActivity` на `AppCompatActivity`,
`Theme.WhichState` — с `android:Theme.Material.Light.NoActionBar` на
`Theme.AppCompat.Light.NoActionBar`. Сам вызов `setApplicationLocales()` при переключении
триггерит `recreate()` всех `AppCompatActivity` автоматически (внутренний lifecycle callback
AppCompat) — ручной `recreate()` нигде не понадобился; `MenuViewModel` держит выбранный язык
ещё и в собственном `StateFlow`, чтобы подпись кнопки (EN/RU) обновилась мгновенно, не дожидаясь
пересоздания Activity.

Отдельная проблема архитектуры: `RankPolicy` в `:core:domain` возвращал готовую строку — одну
из 70 английских шуточных фраз результата, а `:core:domain` не имеет права трогать Android-ресурсы
и, значит, не может отдавать локализуемый текст. Решение по прецеденту `GameUiState.isNearMiss`
(Фаза 07: домен/маппер несёт флаг, экран сам резолвит текст) — `RankPolicy.phrase()` стал
`RankPolicy.rank()` и возвращает `Rank(tier, index)`, `RoundSummary`/`ResultsUiState.Loaded` несут
`rankTier`/`rankIndex` (два `Int`, не строку). Сам текст — 7 string-array по 10 фраз
(`feature/results/res/values{,-ru}/rank_phrases.xml`) — резолвится на границе: `ResultsScreen`
через `stringArrayResource`, `AndroidShareCardRenderer` через `context.resources.getStringArray`;
оба читают один и тот же список id-массивов `rankPhraseArrays` (`:feature:results`), чтобы
соответствие «тир → id ресурса» не дублировалось. Заодно нашлось, что share-карточка
(`AndroidShareCardRenderer`) держала весь текст канвы прямо в Kotlin-литералах на английском,
хотя `Context` у неё уже был — теперь каждая строка идёт через `context.getString(...)` и
переиспользует те же ресурсы, что и `ResultsScreen` (заглавные буквы — тем же приёмом, что и
`LegendDot` раньше: ресурс хранит обычный регистр, `.uppercase()` — на месте отрисовки, потому
что кириллическое приведение к верхнему регистру не всегда совпадает с латинским и должно
идти через Kotlin, а не быть зашито в ресурсе).

Не проверено на устройстве (нет эмулятора в этом окружении, как во всех предыдущих фазах):
реальное переключение языка руками — совпадает ли то, что видно на экране, с ожиданием от
`recreate()`; TalkBack-объявление кнопки EN/RU; рендер вариативных Unbounded/Inter с кириллицей
на реальном экране (Pillow проверял только покрытие глифами, не итоговую типографику).

## 11.6 · История раундов

Внеплановая фаза: в исходном v1-списке не значилась, добавлена по прямому запросу пользователя
(план сохранён в памяти `menu-history-feature`) — экран с лучшим результатом и последними 100
попытками по каждому из трёх уровней Classic.

- [x] Домен: `RoundHistoryEntry`, `HistoryAttempt`, `LevelHistory`; `GetHistoryUseCase` — по каждому
      `Level` берёт `bestForLevel`/`recentForLevel(100)` из `RoundHistoryRepository` и достраивает
      перцентиль через `PercentileTable`
- [x] `:core:data`: `RoundDao.getBestForLevel`/`getRecentForLevel` (SQL: `ORDER BY correct DESC,
      wrong ASC, startedAtMs DESC LIMIT 1` для лучшего, `ORDER BY startedAtMs DESC LIMIT :limit`
      для последних), `RoundHistoryRepositoryImpl` и `FakeRoundHistoryRepository` реализуют
      расширенный интерфейс
- [x] Новый модуль `:feature:history` (`HistoryViewModel`/`UiState`/`Screen`/`Route`, вкладки по
      трём уровням, карточка лучшего результата, список последних попыток), подключён в `:app`;
      границы модулей обновлены (`build-logic`, `settings.gradle.kts`, `app/build.gradle.kts`)
- [x] Кнопка «История» вверху слева на главном экране — квадратная, как `LanguageToggleButton`, но
      `Alignment.TopStart`; иконка нарисована вручную на `Canvas` (дуга + стрелка + стрелки часов),
      не из набора Material-иконок

**Готово, когда:** для каждого из трёх уровней Classic видно лучший результат и до 100 последних
попыток; список не включает party-режимы (Describe, Guess State) — они в эту фичу не входят.

**Заметка:** реализация — read-side расширение существующего `RoundHistoryRepository`
(ROADMAP §03), как и предполагалось в памяти: новой таблицы Room не понадобилось, `rounds` уже
хранит всё нужное (`level`, `correct`, `wrong`, `startedAtMs`). `HistoryViewModel` грузит историю
один раз в `init` и держит список в памяти — переключение вкладки (`SelectLevel`) просто
перематчивает уже загруженные данные без повторного похода в Room. Кнопка истории, по требованию
из памяти (`menu-history-feature`), лежит во внешнем `Box`, а не в скроллящемся `Column`, — не
должна уезжать при скролле меню, как и языковая кнопка. Добавление кнопки/иконки в `MenuScreen.kt`
перевалило детект-порог `TooManyFunctions` (12 при пороге 11) — `HistoryButton`/`HistoryIcon`
вынесены в отдельный файл `feature/menu/.../HistoryButton.kt`.

Не проверено на устройстве (нет эмулятора в этом окружении, как во всех предыдущих фазах): переход
из меню в историю и обратно на реальных сохранённых раундах, объявление кнопки и вкладок TalkBack'ом.

## 11.7 · Landscape и edge-to-edge

Внеплановая фаза: раньше приложение проверялось только в портретной ориентации (§1 — «портретная
ориентация»), альбомная не поддерживалась вовсе — контент и карта обрезались бы за пределами
экрана.

- [x] `MenuScreen`/`GameScreen`: `BoxWithConstraints` + ветвление `maxWidth > maxHeight` на
      Portrait/Landscape-компоновки; в landscape меню — герой слева (сжатый `HeroSection`), уровни/
      party/баннер в прокручиваемой колонке справа; в landscape раунда — шапка сверху, затем строка
      из узкой колонки (сжатый `CityPrompt` + `NortheastTiles`) и карты, занимающей остаток ширины
- [x] `navigationBarsPadding()` на нижних/скроллящихся контейнерах, где раньше жёсткая раскладка
      могла упереться в трёхкнопочную или жестовую панель: `Game`, `Menu`, все контент-компоненты
      `:feature:party` (`GameEndContent`, `HandoffContent`, `PlayingContent`, `RoundEndContent`,
      `PartySetupScreen`), `Results` (`BottomActions`), `History`
- [x] Скриншот-тесты на альбомную ориентацию (`qualifiers = "w800dp-h400dp"`, `sdk = 35`):
      `askingForACityLandscape`, `menuScreenLandscape`

**Готово, когда:** меню и классический раунд остаются играбельными и не обрезаются при повороте
телефона в альбомную ориентацию; edge-to-edge контент не перекрывается системной навигацией.

**Заметка:** это не полноценные «планшеты и ландшафт» из «Идей на потом» (там речь про отдельный
макет под планшет) — только про то, чтобы телефон в альбомной ориентации не ломался визуально.
Попутно убран emoji 💀 из `results_shame_line` (`en`/`ru`) — по тому же правилу, что и в коммите
«Strip all comments from source, drop emoji from party mode titles».

Не проверено на устройстве (нет эмулятора в этом окружении, как во всех предыдущих фазах): реальный
поворот телефона (Robolectric-скриншоты проверяют раскладку под конкретный qualifier, а не сам
переход портрет↔альбомная), поведение `navigationBarsPadding()` с настоящей 3-кнопочной или
жестовой навигацией.

---

## 12 · Сервер (после релиза)

- [ ] `:core:network`, анонимная авторизация
- [ ] Отправка раундов: outbox + WorkManager
- [ ] Настоящие перцентили и лидерборды
- [ ] Battle Map с оплатой через RuStore
- [ ] Мультиплеер

## Идеи на потом (не в v1)

- Google Play: flavor `googleplay`, AdMob, Google Play Billing
- Покупка «Убрать рекламу»
- Планшеты и ландшафт
- Экран «Атлас»: карта освоенных штатов и рекорды
- Вибрация и звуки
- Аналитика и краш-репорты (например, AppMetrica) — решено добавить после релиза

---

## Риски

| Риск | Что делаем |
|---|---|
| Hilt / KSP конфликтуют с AGP 9 | проверяем в Фазе 00; запасной вариант — Koin |
| В мелкие штаты трудно попасть пальцем | допуск 12 dp, зум, плитки Северо-Востока |
| Неточный список городов | воспроизводимый скрипт из официального источника + проверки в тестах |
| Рекламный SDK тормозит запуск или падает без сети | ленивая инициализация, прогон в авиарежиме |
| Требования модерации RuStore | политика конфиденциальности и карточка готовятся заранее, в Фазе 11 |

## Открытые вопросы

1. Где разместить политику конфиденциальности (RuStore просит ссылку)?
