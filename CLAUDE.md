# Which State? — Android-версия City Quiz

Приложение **Which State?** — порт веб-игры City Quiz (React + Vite) на Kotlin + Jetpack Compose. Папка и Gradle-проект по-прежнему называются GuessState.
Эталон поведения — исходники сайта: `C:\Users\thund\Downloads\city-quiz-v4\city-quiz\src`.
Архитектура: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md). План работ: [docs/ROADMAP.md](docs/ROADMAP.md).
Общение с пользователем — на русском; код и идентификаторы — на английском.

## Каждая сессия (обязательно)

- Перед первым ответом прочитать память (`MEMORY.md`), `docs/ARCHITECTURE.md` и `docs/ROADMAP.md`.
- В конце каждого ответа добавлять строку: **Прочитал память и архитектуру · фаза: <текущая фаза>**. Текущая фаза — первая в `docs/ROADMAP.md` с невыполненными пунктами. Пользователь по этой строке проверяет, что контекст загружен.

## Правила архитектуры (обязательные)

- Clean Architecture: `feature → domain ← data`. `:app` — единственный composition root.
- `:core:domain` — чистый Kotlin/JVM: без `android.*`, `androidx.*`, Compose, Room и SDK. Разрешены stdlib, kotlinx-coroutines-core, `javax.inject`.
- Правила игры (таймер, проверка ответа, ранги, перцентили, частота рекламы) — только в домене и только с unit-тестами.
- Feature-модули не зависят от `:core:data`, `:core:ads-yandex` и друг от друга. Переходы — лямбды, граф навигации — в `:app`.
- UI: `UiState` + `Action` + `Effect`; `XxxRoute` (ViewModel, эффекты) + `XxxScreen(state, onAction)` без зависимостей.
- Всё платформенное и магазинное (хранилище, реклама, шаринг, платежи, сеть) — за интерфейсами.
- Классы по слоям и их имена (Dto, Entity, Dao, DataSource, RepositoryImpl, UseCase, ViewModel, UiState, UiModel, Action, Effect) — docs/ARCHITECTURE.md §3.1. DTO и Entity не выходят за `:core:data`; ViewModel вызывает только use cases.
- Между экранами передаём идентификаторы (`roundId`), а не объекты.
- Не обходить правила даже ради маленькой фичи. Нарушения ловят Konsist-тесты и convention-плагины.

## Продуктовые решения

- Название приложения — **Which State?**; applicationId и namespace — `ru.xrtz.whichstate`.
- Если описание расходится с сайтом — делаем как на сайте. Явные баги сайта не копируем, а отмечаем.
- Магазин — только RuStore. Реклама — Yandex Mobile Ads (не AdMob): межстраничная между раундами + баннер в меню и на результатах.
- Интерфейс: English по умолчанию + русский (переключение кнопкой в меню), строки в ресурсах. Телефоны, портрет. Светлая тема Editorial Atlas.
- Полный список решений — docs/ARCHITECTURE.md §1.

## Команды

- Полная проверка (сборка + тесты + Konsist + detekt + lint): `./gradlew assembleDebug check`
- Только модуль: `./gradlew :core:domain:test`, `./gradlew :app:testDebugUnitTest` и т.д.
- Статический анализ отдельно: `./gradlew detekt`
- Границы модулей (Gradle-зависимости): `./gradlew checkModuleBoundaries`
- Запуск на устройстве/эмуляторе: `./gradlew :app:installDebug`
