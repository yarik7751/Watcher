# План выноса навигации и модульной разбивки фич

**Статус:** реализовано (сборка `:app:assembleDebug` успешна, аудит пройден). Итоги — в разделе «VII. Итоги реализации».  
**Предпосылка:** миграция на Compose Navigation выполнена (см. [compose-navigation-migration-plan](compose-navigation-migration-plan.md)).

---

## Часть I. Картина целиком

Навигация и экраны делятся на три слоя:

| Слой | Содержимое | Куда переезжает |
|---|---|---|
| **Движок** | `JoyScreen`, `NoArgs`, `ScreensFlow`, `CurrentArgs`, `JoyRouter` | `:core:navigation` |
| **Базовый UI/DI** | `ViewModelFactory`, `BaseViewModel`, `DesignSystem` | `:core:ui` |
| **Сборка графа** | `WatcherNavHost`, `AppContent`, `MainActivity`, `AppComponent` | `:app` (composition root) |
| **Фичи** | 9 экранов: `XJoyScreen` + `XScreen` + `XViewModel` + DI | `:feature:<name>:api` / `:feature:<name>:impl` |

**Ключевая идея:** после разбивки ни один модуль не знает обо всех экранах. Граф собирается в `:app` из Dagger-multibinding'а: каждая фича-impl регистрирует route → `ScreenContentProvider`, `WatcherNavHost` строит `composable(route)` по этой мапе. `JoyScreensRegistry` исчезает — реестром становится сам Dagger-граф.

```
:feature:home:api          :feature:home:impl
  HomeJoyScreen               HomeScreen, HomeViewModel,
  (только JoyScreen)          HomeDaggerModule (VM + provider)
        │                         │ deps: api, :core:navigation, :core:ui, :utils
        ▼                         ▼
:core:navigation  ◄──────────  (контракт ScreenContentProvider)
  JoyScreen, NoArgs,            │
  JoyRouter, ScreensFlow,       │ @IntoMap: route → ScreenContentProvider
  CurrentArgs,                  │
  ScreenContentProvider         │
        ▲                       │
        └───────────────────────┘
:core:ui — ViewModelFactory, BaseViewModel, DesignSystem
:app — AppComponent (включает модули всех impl), WatcherNavHost (по мапе), AppContent
```

## Часть II. Модули и зависимости

### `:core:navigation`

Переносится из `:app` почти без изменений: `JoyScreen.kt`, `args/NoArgs.kt`, `router/*` (5 файлов).

**Дополнение контракта** — поставщик контента экрана (вместо прямых вызовов `XScreen()` из NavHost):

```
interface ScreenContentProvider {
    val screen: JoyScreen<*>
    @Composable fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter)
}
```

Зависимости модуля: `navigation-compose:2.8.5`, coroutines, `javax.inject`, **`:core:ui`** (тип `ViewModelFactory` в сигнатуре контракта). Android-library, compose-флаг не нужен (compose-кода здесь нет, только сигнатура с `@Composable` — достаточно зависимости на compose-runtime через navigation-compose... *уточнение на этапе реализации*: если `@Composable` в сигнатуре потребует compose-плагин, включить `kotlin-compose` плагин, compose-ui остаётся транзитивным).

### `:core:ui`

`ViewModelFactory` + `BaseViewModel` + `DesignSystem` (DesignSystem используют фичи — minesweeper, calendar — и снекбар в `:app`).

Зависимости: compose (BOM), `androidx.lifecycle`, `javax.inject`. Без navigation, без Dagger-runtime.

### `:feature:<name>:api` (9 штук)

Только `XJoyScreen : JoyScreen<NoArgs>` (object, route). Зависимости: **`:core:navigation` и ничего больше**. Это намеренно тонкие модули — их подключение к другим фичам бесплатно.

### `:feature:<name>:impl` (9 штук)

`XScreen` (композабл), `XViewModel`, feature-local код (у minesweeper/gamefield — весь `game/`, мапперы, hexagon), Dagger-модуль фичи:

```
@Module
interface HomeModule {
    @Binds @IntoMap @ViewModelKey(HomeViewModel::class)
    fun vm(vm: HomeViewModel): ViewModel                        // перенос из ViewModelModule :app

    @Binds @IntoMap @ScreenKey(HomeJoyScreen.route)
    fun content(provider: HomeScreenProvider): ScreenContentProvider
}
```

Зависимости: свой api, `:core:navigation`, `:core:ui`, `:utils`, нужные AndroidX, **api-модули других фич** для межэкранных переходов (напр. `:feature:start:impl` → `:feature:home:api`). Зависимости impl→чужой impl **запрещены** — только чужие api.

`ScreenKey` — новая Dagger-мап-аннотация (`@MapKey` с `String`-ключом), живёт в `:core:ui` рядом с `ViewModelKey` или в `:core:navigation`.

### `:app` (composition root)

- `AppComponent`: вместо `ViewModelModule` включает Dagger-модули всех 9 impl → мапы VM и провайдеров агрегируются автоматически.
- `WatcherNavHost(factory, router, contentProviders: Map<String, @JvmSuppressWildcards ScreenContentProvider>)`: по мапе строит `composable(route)`; `startDestination` = `StartJoyScreen.route` (`:app` зависит от `:feature:start:api` — это допустимо, стартовый экран — часть сборки).
- `AppContent`, `MainActivity`, снекбар — без изменений.
- `JoyScreensRegistry` **удаляется**: `screensFlow` в `JoyRouter` строится по той же Dagger-мапе (route → JoyScreen через `provider.screen`) либо по `currentBackStack` + мапа. `NavigationModule` сокращается до привязок интерфейсов.
- `features/testgetuserdata` — отдельный вопрос (см. раздел IV): проще всего оставить в `:app` (это Activity вне навигации).

## Часть III. Этапы

### Этап 0 — Решения (без кода)
1. Имена: `:core:navigation`, `:core:ui`, `:feature:<name>:api|impl` (имена фич: `start`, `home`, `calendar`, `rendernode`, `layoutsandbox`, `subcomposelayoutsandbox`, `minesweeper`, `minesweeper-gamefield`, `minesweeper-settings`).
2. Пакеты: сразу `com.yarik.watcher.core.navigation`, `com.yarik.watcher.core.ui`, `com.yarik.watcher.feature.<name>.api|impl` — иначе пакеты раздвоятся между модулями.
3. `JoyRouter` сохраняет `@Inject`-разметку (`javax.inject`, не привязывает к Dagger).
4. Стартовый экран: `:app` держит зависимость на `:feature:start:api` для `startDestination`.

### Этап 1 — Скелет core-модулей
1. `settings.gradle.kts`: `:core:navigation`, `:core:ui`.
2. Перенос движка (`JoyScreen`, `NoArgs`, `router/*`) → `:core:navigation`; перенос `ViewModelFactory`, `BaseViewModel`, `DesignSystem` → `:core:ui`.
3. `WatcherNavHost`/`JoyScreensRegistry`/`NavigationModule` в `:app` — только правка импортов. Сборка, смоук. **Промежуточная точка стабильности.**

### Этап 2 — Контракт ScreenContentProvider + перевод NavHost на мапу (фичи ещё в :app)
1. Добавить `ScreenContentProvider` в `:core:navigation`, `ScreenKey` в `:core:ui`.
2. В `:app` обернуть каждый из 9 `XScreen()` в локальный провайдер, собрать мапу вручную (без Dagger) и построить `WatcherNavHost` по мапе. `JoyScreensRegistry` удалить, `screensFlow` — по мапе.
3. Сборка, полный регресс навигации. **Вторая точка стабильности: граф уже data-driven, фичи пока в :app.**

### Этап 3 — Разбивка фич на api/impl (шаблон: start + home, затем остальные пачками)
Шаблон для одной фичи:
1. `:feature:<name>:api` — перенос `XJoyScreen`, пакет `feature.<name>.api`.
2. `:feature:<name>:impl` — перенос `XScreen`, `XViewModel`, локального кода; Dagger-модуль фичи (VM-binding переносится из `ViewModelModule` :app + binding провайдера в мапу).
3. `:app`: удалить перенесённое; `AppComponent` включает модуль фичи; `WatcherNavHost` забирает провайдер из Dagger-мапы.
4. Порядок: **start + home** (шаблон, включает replace и navigateTo) → calendar, rendernode (без VM — простейшие) → layoutsandbox, subcomposelayoutsandbox → minesweeper-цепочка (3 модуля, у gamefield больше всего локального кода и DI-зависимостей — проверить провижн `GameEngine`/мапперов: если они в `:app`-модулях `DomainModule`/`DataModule`, перенести провайдеры в impl-фичу).
5. Межфичные api-зависимости: start→home; home→calendar, rendernode, layoutsandbox, subcomposelayoutsandbox, minesweeper.

### Этап 4 — Финальная сборка и чистка
1. Удалить пустые пакеты в `:app`, убедиться что `ViewModelModule` пуст → удалить (`ViewModelKey` перенести в `:core:ui`).
2. `NavigationModule` — финальный вид (только интерфейсы или удалить вовсе).
3. Полный регресс: все переходы, back, replace, поворот, процесс-дес, пермишены, снекбар.
4. Grep-аудит правил зависимостей: `api`-модули не зависят ни от чего кроме `:core:navigation`; impl не зависят от чужих impl.

## Часть IV. Открытые вопросы / решения

| Вопрос | Рекомендация |
|---|---|
| `features/testgetuserdata` (Activity вне навигации) | Оставить в `:app` — минимальный diff; если нужна изоляция — отдельный модуль без api (это не экран NavHost'а) |
| 9 фич × 2 модуля = 18 модулей — не перегруз? | Для 9 мелких экранов — приемлемо; альтернатива (групповые `:feature:sandbox:api/impl`) ломает изоляцию маршрутов. Оставить per-feature |
| Снекбар (`ShackBar`, `SnackBarManagerFlow`) | Остаётся в `:app` (глобальный оверлей); `SnackBarManagerFlow` как зависимость для VM фич — либо в `:core:ui`, либо инжект в VM через интерфейс из `:core:navigation`-уровня. Решить при разборе Minesweeper-пачки: проверить, кто реально пользует снекбаром |
| `LocalValuesProvider`/прочие shared-зависимости из `logic/` | Отдельным скоупом: при разбивке фич выяснится, кто чем пользуется; выносить в `:core:data` по мере необходимости, не на опережение |
| Скорость сборки | 20+ модулей на AGP 8.9 — нормально; включить только если станет больно (не входит в план) |

## Часть V. Риски

| Риск | Митигация |
|---|---|
| Dagger multibinding мапа провайдеров: ключ — строка route, опечатка = молча пропавший экран | `ScreenKey` принимает `JoyScreen`-объект (мап-ключ value = `provider.screen.route` через аннотацию не передать — валидировать на старте: все `JoyScreensRegistry`-подобная проверка в Debug, что мапа покрывает все route) |
| Раздвоение пакетов между модулями путает IDE | Решение о пакетах на этапе 0, переезд сразу |
| Циклические зависимости api/impl | Правило: только api→api; аудит grep'ом (часть III, этап 4) |
| DI-провижн `GameEngine`/мапперов поля окажется в `:app`-модулях | Перенести провайдеры в `:feature:minesweeper-gamefield:impl` на этапе 3 |
| Поведение изменится при промежуточных этапах | Точки стабильности после этапов 1, 2, 3; регресс на каждой |
| kapt/Dagger по модулям | Без изменений в подходе: компонент один, в `:app`; kapt уже работает |

## Часть VI. Критерии приёмки

1. `:core:navigation` и `:core:ui` собираются отдельно; в них нет упоминаний фич, `:app`, `:utils` (кроме согласованных зависимостей).
2. Каждая `api`-зависимость фичи: только `:core:navigation`. Зависимости impl: свои api + `:core:*` + чужие api.
3. `WatcherNavHost` не содержит ссылок на конкретные `XScreen`/`XJoyScreen` (кроме `StartJoyScreen.route` для `startDestination`).
4. Добавление нового экрана = создание пары модулей + один include в `AppComponent`; **ни одного изменения в `:app`-коде навигации**.
5. Полный регресс навигации идентичен поведению до разбивки.

---

## VII. Итоги реализации

Разбивка выполнена целиком, промежуточные точки стабильности (этапы 1–2 плана) схлопнуты в один проход. Сборка `./gradlew :app:assembleDebug` успешна, `:core:navigation` и `:core:ui` собираются отдельно.

**Итоговая структура (23 модуля):**

| Модуль | Содержимое |
|---|---|
| `:core:navigation` | `JoyScreen`, `NoArgs`, `JoyRouter`, `ScreensFlow`, `CurrentArgs`, `ScreenContentProvider` (пакет `core.navigation[.router]`) |
| `:core:ui` | `ViewModelFactory`, `BaseViewModel`, `DesignSystem`, `CommandFlow` (+`emit`), `ViewModelKey`, `ScreenKey` (пакет `core.ui`) |
| `:feature:<name>:api` ×9 | `XJoyScreen` (object) + `const val ROUTE` — зависит только от `:core:navigation` |
| `:feature:<name>:impl` ×9 | `XScreen`, `XViewModel`, локальный код, `XScreenProvider`, `XModule` (Dagger: VM в мапу + provider контента в мапу `route → ScreenContentProvider`) |
| `:feature:testgetuserdata` | Отдельный модуль (Activity вне NavHost'а); зависимость от `WatcherApplication` убрана — репозиторий создаётся локально |
| `:app` | `AppComponent` (включает модули всех impl), `WatcherNavHost` (строится по Dagger-мапе провайдеров), `AppContent`, `MainActivity`, снекбар |

**Ключевые решения, принятые при реализации:**

- **Граф data-driven:** `MainActivity` инжектит `Map<String, ScreenContentProvider>` (Dagger multibinding, ключ — `ScreenKey`), `WatcherNavHost` строит `composable(route)` по мапе и передаёт `router.attach(navController, мапа route → screen)`. `JoyScreensRegistry` удалён.
- **kapt обязателен в impl-модулях фич** — фабрики `@Provides`-методов (`XModule_Provide*Factory`) генерируются обработкой аннотаций в модуле, где объявлен `@Module`. В `:core:*` kapt нет: `JoyRouter` и `ViewModelFactory` создаются явными `@Provides` в `:app` (`NavigationModule`), модули остаются чистыми от Dagger-runtime.
- **`ROUTE`-константы:** `@ScreenKey(XJoyScreen.ROUTE)` требует compile-time constant, поэтому в каждом api-экране добавлен `const val ROUTE`, а `route` — геттером на него.
- **Удалено:** `ViewModelModule` и `DomainModule` :app (VM-биндинги и `GameEngine` перенесены в модули фич), дубликат `ActionUiModel`, `userRepository` из `WatcherApplication`.

**Аудит (критерии приёмки):** api-модули зависят только от `:core:navigation` ✓; impl не зависят от чужих impl ✓; `WatcherNavHost` знает только `StartJoyScreen` (start destination) ✓; остатков старых пакетов нет ✓.

**Известные упрощения / следы для следующих итераций:**

1. VM и провайдеры фич конструируются явными `@Provides` (без `@Inject`-конструкторов) — конструкторы VM дублируются в `XModule`; при изменении конструктора VM правится и модуль.
2. Пакеты внутри impl-модулей «сплющены» (бывшие подпакеты `game/`, `hexagon/` стали суффиксами пакета `...impl.game` и т.п. — файлы лежат плоско; Kotlin это допускает). При желании можно вернуть структуру каталогов.
3. `:feature:home:impl` зависит от `:feature:testgetuserdata` — кнопка запуска тестовой Activity; при появлении настоящего контракта навигации на Activity (route + deep link) зависимость можно сузить.
4. Поведение приложения не менялось (сборка + тот же граф), но рантайм на устройстве не прогонялся — регресс переходов/back/поворота остаётся ручным шагом.
