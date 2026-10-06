# План миграции на Jetpack Compose Navigation

**Статус:** реализовано (сборка `:app:assembleDebug` успешна). Итоги — в разделе «7. Итоги реализации».  
**Цель:** перейти с Cicerone + Fragment-на-экран на Jetpack Compose Navigation, **сохранив контракт экранов** (`JoyScreen<A : Parcelable>` + `JoyRouter`).

---

## 1. Текущее состояние

- Один `AppCompatActivity` (`MainActivity`), Cicerone 7.1, каждый экран — `Fragment` (`BaseComposeFragment`) с `ComposeView`.
- `JoyScreen<A : Parcelable>` — контракт экрана: `args` + `screen: FragmentScreen` (Cicerone).
- `JoyRouter` — обёртка над Cicerone `Router` со стеком `screensFlow: MutableStateFlow<List<JoyScreen<*>>>`; реализует `ScreensFlow` и `CurrentArgs`.
- 9 экранов, все с `NoArgs` (инфраструктура аргументов есть, но нигде не используется).
- DI — ручной dagger-android (без Hilt), ViewModel через multibinding `ViewModelFactory`; каждый фрагмент вручную добавлен в `FragmentInject.kt`.
- Табы (`TabsCiceroneHolder`, `TabsManager`, `TabsObserver`) — недостроенный стаб, в коде не вызывается. `ScreensObserver` закомментирован.
- `setResultListener/sendResult` в `JoyRouter` — ни одного вызова. Диплинков нет. `TestGetUserActivity` — отдельная Activity вне Cicerone (вне скоупа).
- Navigation-Compose в зависимостях **отсутствует**; Compose BOM 2024.09.00 (старый), Kotlin 2.2.0, minSdk 26 / targetSdk 36.

## 2. Целевая архитектура: как будет выглядеть навигация

### 2.1. Хост

`MainActivity` теряет `FragmentManager`/Cicerone-навигатор. В `onCreate` — `setContent { WatcherNavHost(...) }` с `NavHost`, глобальным `Scaffold` (оверлей снекбара `@id/shackBar` и будущий bottom-bar) и `MaterialTheme` (тема сейчас оборачивает контент в `BaseComposeFragment` — переезжает в Activity).

### 2.2. Контракт экранов сохраняется

```kotlin
interface JoyScreen<A : Parcelable> {
    val args: A
    val route: String          // вместо Cicerone Screen — уникальный route-идентификатор
}
```

- Каждый существующий `object XJoyScreen : JoyScreen<NoArgs>` **остаётся как есть** плюс получает `route` (константа вида `"calendar"`, `"minesweeper/game"`). Все точки навигации в ViewModel/композаблах продолжают ссылаться на эти объекты — API не меняется.
- Типизированные аргументы: при появлении экранов с аргументами маршрут объявляется как `"route/{arg}"`, args кладутся в `SavedStateHandle` текущего back stack entry. Вариант на будущее — `@Serializable` data-class роуты (kotlinx-serialization уже подключён, Navigation 2.8+ поддерживает type-safe навигацию); на первом этапе достаточно строковых роутов + `Parcelable` в `SavedStateHandle`, чтобы не менять `A : Parcelable` границу контракта.

### 2.3. JoyRouter — фасад над NavController

`JoyRouter` сохраняет публичные методы, реализация меняется на делегирование `NavController`:

| Метод JoyRouter (было → Cicerone) | Становится |
|---|---|
| `navigateTo(screen)` | `navController.navigate(screen.route)` |
| `replaceScreen(screen)` | `popBackStack()` + `navigate(route)` (или `navigate` с `popUpTo(current)` + `saveState=false`) |
| `newRootScreen(screen)` | `navigate(route) { popUpTo(0) { inclusive = true } }` |
| `backTo(screen)` | `popBackStack(screen.route, inclusive = false)` |
| `exit()` | `popBackStack()` |
| `newChain / newRootChain` | последовательные `navigate` / `newRootChain` = `newRootScreen` + чейн |
| `finishChain()` | `popBackStack(route первого в чейне, false)` либо `navigateUp()` — семантика фиксируется при миграции |
| `setResultListener / sendResult` | `currentBackStackEntry.savedStateHandle` по ключу (listener — `getStateFlow(key)` в VM/композабле) |

- `screensFlow: StateFlow<List<JoyScreen<*>>>` сохраняется: строится из `navController.currentBackStackEntryFlow` + реестр `route → JoyScreen` (все `JoyScreen`-объекты регистрируются в одном месте — `JoyScreensRegistry`, также используется для `CurrentArgs`).
- `CurrentArgs.get<A>()` читает args текущего entry из реестра/SavedStateHandle — сигнатура не меняется, никто не замечает переезда.

Back-обработка: глобальный `OnBackPressedCallback` в Activity заменяется стандартным поведением `NavController` (`navigateUp()` / `Activity.finish()` на пустом стеке) — поведение идентично текущему.

### 2.4. ViewModel и DI — без изменений графа

- Dagger-граф и `ViewModelModule` (multibinding) **не трогаем**. Вместо `by viewModels()` во фрагменте композабл получает VM так: `viewModel(factory = viewModelFactory, key = ...)` — `viewModelFactory` приходит в композабл из Activity (существующий `ViewModelFactory` из `di/module/viewmodel/`). `viewmodel-compose` уже в зависимостях.
- `JoyRouter` по-прежнему инжектится во ViewModel/композаблы как сейчас (`JoyRouter` в `BaseComposeFragment` уже инжектится) — меняется только его внутренняя реализация и способ получения `NavController` (фасад держит ссылку, выставленную при создании `NavHost`).

### 2.5. Карта графа (как будет выглядеть NavHost)

```
startDestination = StartJoyScreen.route

start          → StartScreen()        (замена на home после пермишенов)
home           → HomeScreen()         (кнопки → 6 переходов)
calendar       → CalendarScreen()
rendernode     → RenderNodeScreen()
layoutsandbox  → LayoutSandboxScreen()
subcomposelayoutsandbox → SubcomposeLayoutSandboxScreen()
minesweeper    → MinesweeperScreen()
minesweeper/game → MinesweeperFieldScreen()
minesweeper/settings → MinesweeperSettingsScreen()   // пока не в графе: экран мёртвый
```

Каждый destination — `composable(route = XJoyScreen.route) { XScreen() }`, где `XScreen()` — бывший `ScreenContent()` фрагмента, переехавший в top-level композабл. Вложенность `minesweeper/*` — либо flat-роуты, либо nested `navigation("minesweeper")` — решение на этапе миграции (рекомендация: nested graph, т.к. `newChain/finishChain` семантически про вложенный флоу).

## 3. Этапы миграции

### Этап 0 — Подготовка (отдельный PR)
1. Добавить `androidx.navigation:navigation-compose` (в идеале обновить Compose BOM с 2024.09.00 до актуальной — навигация 2.8.x, type-safe routes).
2. Вынести `MaterialTheme` + инсет-паддинги из `BaseComposeFragment` в Activity-level `setContent` (общая обёртка для всех экранов).
3. Решить судьбу мёртвого кода:
   - `MinesweeperSettingsJoyScreen` — либо подключить к графу, либо удалить;
   - `ScreensObserver`, `TabsObserver`, `TabsManager`, `TabsCiceroneHolder`, `BottomNavigationMenuItem` — закомментированный/невызываемый стаб табов: **не мигрировать**, либо удалить, либо оставить как референс для этапа 4;
   - `setResultListener/sendResult` — реализовать через `SavedStateHandle` сразу или удалить до появления первого реального use-case.
4. Зафиксировать текущее поведение (ручной смоук: стек, back, replace) как эталон для приёмки.

### Этап 1 — Фундамент Navigation (PR: инфраструктура без смены экранов)
1. `JoyScreen`: добавить `val route: String`, удалить `val screen: FragmentScreen` (последним, после миграции всех экранов — на этом этапе держим оба поля или держим `screen` deprecated).
2. `JoyScreensRegistry`: `route → JoyScreen` для всех 8 живых экранов.
3. Новая реализация `JoyRouter` поверх `NavController` (инжектится как синглтон, `NavController` выставляется из `NavHost`-композабла через `DisposableEffect`).
4. `NavHost` в Activity с `startDestination = StartJoyScreen.route`, внутри — временный мост: каждый destination рендерит `AndroidFragment`-обёртку над существующим фрагментом **или** параллельный запуск: Cicerone-навигатор выключается, все navigate идут через NavController, а фрагменты пока создаются через `FragmentScreen` внутри `AndroidFragment`. Это позволяет переключить движок навигации одним PR, не переписывая экраны.
   - *Альтернатива (больший взрыв, меньше кода):* сразу мигрировать все экраны. Рекомендация — мост через `AndroidFragment`, т.к. `fragment-compose` уже объявлен в зависимостях.
5. Приёмка этапа: вся навигация идёт через NavController, экраны по-прежнему фрагменты, поведение = эталону этапа 0.

### Этап 2 — Конвертация экранов (по одному PR на экран или малые пачки)
Порядок — от листьев к корню, чтобы каждый PR был маленьким:
1. **Пакет A (без ViewModel / простые):** Calendar, RenderNode.
2. **Пакет B (с ViewModel):** LayoutSandbox, SubcomposeLayoutSandbox.
3. **Пакет C (чейн):** Minesweeper → MinesweeperField (здесь же решение про nested graph).
4. **Пакет D:** Home, затем Start.

Шаблон конвертации одного экрана:
1. `XFragment.ScreenContent()` → top-level `@Composable fun XScreen(viewModel: XViewModel = viewModel(factory = ...))` в `features/x/XScreen.kt`.
2. Permission-launcher из `StartFragment` (`rememberLauncherForActivityResult`) переносится как есть в `StartScreen`.
3. `CommandFlow` / `observe` — без изменений.
4. Удалить: `XFragment`, `FragmentInject`-запись, `XJoyScreen.screen` (FragmentScreen), `getFragmentInstanceWithArgs` для этого экрана; в `NavHost` destination заменяется с `AndroidFragment` на composable.
5. `AndroidManifest`: разрешения/экспорт не затрагиваются; `TestGetUserActivity` не трогаем (вызывается интентом, можно позже перевести на route с deep link, вне скоупа).

### Этап 3 — Удаление Cicerone и фрагментной инфраструктуры (PR: чистка)
1. Удалить зависимость `cicerone`, `BaseComposeFragment`, `FragmentInject.kt`, `ViewModelCreation.kt` (фрагментные делегаты), `NavigationArgs.kt`/`NoArgs.kt` (если контракт аргументов полностью переехал на SavedStateHandle), `activity_main.xml` (перейти на чистый Compose, сохранив `@id/shackBar` как Compose-оверлей).
2. Проверить: ни одного `FragmentManager`-вызова в Activity; back = штатный `NavController`; `screensFlow` живёт из `currentBackStackEntryFlow`.
3. Полный регресс: все переходы, back на каждом экране, поворот/процесс-дес (VM не теряются), пермишены на Start.

### Этап 4 — После миграции (вне текущего скоупа, отдельными задачами)
- Bottom navigation: `Scaffold` + bottom bar, nested `NavHost` на таб (код табов писать заново, текущий стаб Cicerone-табов не переиспользуется).
- Результаты между экранами: `SavedStateHandle` + `getStateFlow` (API `JoyRouter.setResultListener/sendResult` сохраняем как фасад).
- Типизированные роуты `@Serializable` при первом экране с аргументами.
- Диплинки/пуш-навигация (потребует `FirebaseMessagingService` — сейчас не объявлен).

## 4. Риски и ограничения

| Риск | Митигация |
|---|---|
| Compose BOM 2024.09.00 старый — возможны конфликты с navigation 2.8.x | Обновить BOM на этапе 0, собрать полный регресс UI |
| Процесс-дес/поворот: VM привязаны к фрагментам (`viewModels()`) | Перевод на `viewModel(factory = viewModelFactory)` в composable сохраняет привязку к back stack entry; проверить на Start/Home после пакета D |
| Разница семантики `replaceScreen` (Cicerone) vs pop+navigate | Зафиксировать в `JoyRouter`-реализации; покрыть smoke-сценариями replace (Start→Home) |
| `finishChain/newChain` сейчас не используются — семантика «на бумаге» | Определить точное поведение при первом реальном использовании; до этого — простейшая реализация |
| Мёртвый код табов может запутать при миграции | Явно удалить/пометить на этапе 0, не тащить в новую навигацию |
| Пермишены на Start-экране | Переносятся в composable без изменений; проверить после удаления фрагмента |

## 5. Критерии приёмки миграции

1. `JoyScreen<A : Parcelable>` + `JoyRouter` — единственные точки входа навигации, сигнатуры не изменились (кроме удаления `screen: FragmentScreen` и добавления `route`).
2. В проекте нет Cicerone, фрагментов экранов и `FragmentManager` в Activity.
3. Все 8 живых экранов — composable destinations; `MinesweeperSettings` — решён (в графе или удалён).
4. Back, replace, поворот, процесс-дес, пермишены — соответствуют эталону этапа 0.
5. DI-граф (`AppComponent`, `ViewModelModule`) не переписывался — только удалились фрагментные инжекты.

---

### Сводка: как будет выглядеть навигация после

- Всё в одном `NavHost` в `MainActivity` (`setContent`), граф — плоский список `composable(route)` с возможным nested graph для minesweeper-чейна; `startDestination` = Start.
- Экраны инициируют переходы только через инжектированный `JoyRouter` (те же глаголы: `navigateTo`, `replaceScreen`, `exit`, …), внутри — `NavController`.
- Идентификаторы экранов — те же объекты `XJoyScreen` с полем `route`; аргументы (когда появятся) — `Parcelable` через `SavedStateHandle`, типизированный доступ через `CurrentArgs`.
- ViewModel — прежний Dagger `ViewModelFactory`, получаемый в composable через `viewModel(factory = ...)`.
- Тема, инсеты и глобальный снекбар — на уровне Activity-обёртки вокруг `NavHost`.

---

## 7. Итоги реализации

Миграция выполнена целиком, без промежуточного моста `AndroidFragment` (этапы 1–3 схлопнуты в одну реализацию, т.к. план был реализован за один проход). Сборка `./gradlew :app:assembleDebug` успешна.

**Ключевые файлы:**

| Файл | Что |
|---|---|
| `navigation/JoyScreen.kt` | Контракт: `args` + `route` (вместо Cicerone `screen`) |
| `navigation/JoyScreensRegistry.kt` | Реестр route → JoyScreen (9 экранов) |
| `navigation/router/JoyRouter.kt` | Фасад над `NavController`: прежние глаголы + `attach`/`detach`, `screensFlow` из `currentBackStack`, `CurrentArgs` из зеркала стека |
| `navigation/WatcherNavHost.kt` | Граф: 9 flat-destinations, `startDestination = Start`; `router.attach(navController)` в `DisposableEffect` |
| `AppContent.kt` | Тема, системные бары (бывший `BaseComposeFragment`), снекбар-оверлей — обёртка вокруг NavHost |
| `MainActivity.kt` | `setContent { WatcherAppContent(...) }`, без `FragmentManager`, back — штатный `NavController` |
| `features/*/XScreen.kt` | 9 экранов — top-level composable; VM через `viewModel(factory = viewModelFactory)` |

**Принятые решения по мёртвому коду:**

- `MinesweeperSettingsJoyScreen` — мигрирован в граф как destination (`minesweeper/settings`); кнопки на него пока нет (как и раньше), но экран живой и адресуемый.
- Таб-стаб (`TabsCiceroneHolder`, `TabsManager`, `TabsObserver`, `ScreensObserver`, `BottomNavigationMenuItem`, `NavigationBindModule`) — удалён: не использовался и зависел от Cicerone.
- Result API: `setResultListener/sendResult` (Cicerone) заменены на `JoyRouter.sendResult(key, data)` → пишет в `previousBackStackEntry.savedStateHandle` и `JoyRouter.resultFlow<T>(key)` → `getStateFlow` текущего entry. Call sites не было, сигнатуры не ломают ничего.
- `NavigationArgs.kt` (FragmentScreen-фабрика) удалён; `NoArgs` оставлен (граница контракта `JoyScreen<A : Parcelable>`).

**Зависимости:** Cicerone, `fragment-ktx`, `fragment-compose` удалены; добавлен `androidx.navigation:navigation-compose:2.8.5` (Navigation не входит в Compose BOM — версия задана явно); Compose BOM поднят `2024.09.00` → `2024.12.01`. Dagger-граф (`AppComponent`, `ViewModelModule`) не менялся, кроме удаления фрагментных inject-методов и таб-провайдеров.

**Изменения поведения (осознанные):**

1. Поворот/процесс-дес: экран сохраняется (NavHost восстанавливает back stack), раньше `replaceScreen(Start)` в `onCreate` сбрасывал на Start.
2. `MainActivity` больше не вызывает `router.replaceScreen(StartJoyScreen)` — Start это `startDestination`.
3. Back на корне стека — штатное поведение Navigation (finish activity), эквивалентно прежнему.
4. `finishChain()` реализован как возврат к start destination графа (семантика «выход из флоу»); метод нигде не вызывается.

**Осталось за скоупом (этап 4):** bottom navigation (писать заново), type-safe `@Serializable` роуты при первом экране с аргументами, диплинки/пуш-навигация, расширение `ViewModelFactory` до `CreationExtras` при первом VM с `SavedStateHandle`.
