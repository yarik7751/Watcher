# Список строк для выноса в strings.xml по модулям

**Статус:** список (код не пишется).

Полный скан захардкоженных UI-строк во всех модулях (`app`, `core/*`, `feature/*`, `utils`).
Технические литералы (log-теги, `DateTimeFormatter`-паттерны `"EEEE"/"LLLL"`, локаль `"ru"`, `measureText("88")`, разделитель `", "`, тестовые данные `UserDataSource`, числа в ячейках сапёра, ROUTE-константы навигации) **не выносятся** — они не пользовательский текст.

Локализация: default `values/strings.xml` = текущее содержимое (смесь ru/en, как сейчас на экранах); `values-en` — опционально, отдельной задачей. Форматирование: `%1$d`/`%1$s` вместо интерполяции.

---

## `:feature:start:impl` — новый `res/values/strings.xml`

| Имя ресурса | Значение | Где |
|---|---|---|
| `start_screen_title` | Start screen | StartScreen |

## `:feature:home:impl` — новый

| Имя ресурса | Значение | Где |
|---|---|---|
| `home_title` | Home | HomeScreen |
| `home_button_simple_counter` | SimpleCounter | HomeScreen |
| `home_button_simple_atomic_counter` | SimpleAtomicCounter | HomeScreen |
| `home_button_reordering` | Reordering | HomeScreen |
| `home_button_buffer` | Buffer(wait, notify) | HomeScreen |
| `home_button_semaphore` | Semaphore | HomeScreen |
| `home_button_calendar` | Calendar widget | HomeScreen |
| `home_button_rendernode` | RenderNode | HomeScreen |
| `home_button_layout_sandbox` | Layout Sandbox | HomeScreen |
| `home_button_subcompose_sandbox` | SubcomposeLayout Sandbox | HomeScreen |
| `home_button_test_user_data` | Test get user data | HomeScreen |
| `home_button_minesweeper` | Minesweeper | HomeScreen |

## `:feature:calendar:impl` — новый

| Имя ресурса | Значение | Где | Примечание |
|---|---|---|---|
| `calendar_weekday_mon` | ПН | CalendarMath (`WEEKDAY_LETTERS_MONDAY_FIRST`) | массив из 7, см. ниже |
| `calendar_weekday_tue` | ВТ | CalendarMath | |
| `calendar_weekday_wed` | СР | CalendarMath | |
| `calendar_weekday_thu` | ЧТ | CalendarMath | |
| `calendar_weekday_fri` | ПТ | CalendarMath | |
| `calendar_weekday_sat` | СБ | CalendarMath | |
| `calendar_weekday_sun` | ВС | CalendarMath | |

Альтернатива: `<string-array name="calendar_weekday_letters_monday_first">` из 7 `<item>` — удобнее текущего `listOf`. Названия месяцев/дней («ОКТЯБРЬ», «Вторник») — НЕ выносим: идут из `java.time` с локалью `ru` (`DateTimeFormatter` "LLLL"/"EEEE"), это уже локализовано системой. Рисуется в Bitmap через `Canvas.drawText` — для доступа к ресурсам `CalendarDraw.draw(context = ...)` уже принимает `Context`.

## `:feature:rendernode:impl` — новый

| Имя ресурса | Значение | Где | Примечание |
|---|---|---|---|
| `rendernode_title` | RenderNode | RenderNodeScreen + `drawNodeContent` (canvas) | два места, один ресурс |
| `rendernode_counter_text` | Нажато: %1$d | Counter | formatted |
| `rendernode_counter_button` | Нажать | Counter | |
| `rendernode_demo_hint` | контент записан один раз — крутим узел каждый кадр | RenderNodeDemo | |
| `rendernode_demo_subtitle` | записано 1 раз — только поворот | `drawNodeContent` (canvas) | |
| `rendernode_unsupported` | RenderNode доступен только с Android 10 (API 29) | RenderNodeDemo | |

## `:feature:layoutsandbox:impl` — новый

| Имя ресурса | Значение | Где | Примечание |
|---|---|---|---|
| `layoutsandbox_button_title` | Кнопка %1$d | LayoutSandboxViewModel | formatted, генерируется в цикле 1..10 |

## `:feature:subcomposelayoutsandbox:impl` — новый

| Имя ресурса | Значение | Где | Примечание |
|---|---|---|---|
| `subcompose_loading` | Loading ... | SubcomposeLayoutSandboxScreen | сейчас `TextOrResource.Text` — заменить на `TextOrResource.Resource`, `LoadingIndicator` уже принимает `TextOrResource` |

## `:feature:minesweeper:impl` — новый

| Имя ресурса | Значение | Где |
|---|---|---|
| `minesweeper_start_game` | Start game | MinesweeperScreen |

## `:feature:minesweeper-gamefield:impl` — новый

| Имя ресурса | Значение | Где | Примечание |
|---|---|---|---|
| `minesweeper_cell_flag` | ⚑ | HexCellUiModelMapper (`FLAG_TEXT`) | символ, не локализуется — можно оставить и как const; вынос для единообразия |
| `minesweeper_cell_mine` | ✸ | HexCellUiModelMapper (`MINE_TEXT`) | аналогично |

Числа в ячейках (`HexagonGridMap`, `Text(text = it)`) — не строковые ресурсы.

## `:feature:minesweeper-settings:impl` — новый

| Имя ресурса | Значение | Где |
|---|---|---|
| `minesweeper_settings_title` | Minesweeper Settings | MinesweeperSettingsScreen |

## `:feature:testgetuserdata` — новый

| Имя ресурса | Значение | Где |
|---|---|---|
| `test_user_data_upload` | UPLOAD DATA | TestGetUserActivity |

Имена/телефоны в `UserDataSource` — тестовые данные, не UI-строки, не выносим.

## `:app`, `:core:*`, `:utils` — без изменений

- `:app`: `strings.xml` уже содержит строки виджетов; новых литералов нет (снекбар получает `SnackBarData.message: TextOrResource` — ресурсная ветка уже поддерживается).
- `:utils`: `date_today`/`date_yesterday` уже есть; `TextOrResource` — контракт, не строки.
- `:core:navigation` / `:core:ui`: пользовательских строк нет.

---

## Решения и замечания

1. **Дубликаты между модулями** — законны: `RenderNode` в home (кнопка) и rendernode (заголовок) — разные ресурсы в разных модулях, сильная изоляция важнее переиспользования на этом этапе.
2. **Строки, рисующиеся в Canvas** (календарь, RenderNode-узел): ресурсы читаются через уже проброшенный `Context`.
3. **Итого:** 10 новых `strings.xml`, ~35 строковых ресурсов, 2 форматированных (`%1$d`).
4. **Не входит в скоуп:** `values-en`, plural-строки (не нужны), переименование существующих ресурсов `:app`.
