# Android Graphics — шпаргалка

Конспект по материалу [Android Graphics.md](</Users/yaroslavlevshunov/Documents/Android/Android/Android/Android Graphics.md>) (§ 2.1–2.8) + интернет-источники.
Главная мысль: **кадр делают несколько участников параллельно; оптимизация — назвать того, кто не успел.**

## Путь кадра одной строкой

```
VSync → Choreographer → Main (measure/layout/draw = ЗАПИСЬ Display List)
      → sync barrier (staging→active) → RenderThread (dequeueBuffer → Skia → queueBuffer + fence)
      → GPU (пиксели) → SurfaceFlinger (VSYNC_SF: acquire → HWC решает DEVICE/CLIENT → present) → ЭКРАН
```

Бюджет кадра: 60 Гц ≈ 14 мс, 90 Гц ≈ 9 мс, 120 Гц ≈ 6.5 мс.

## Участники

| Кто | Роль (мастерская) | Что делает |
|---|---|---|
| Choreographer | диктор | будит Main по VSYNC, гонит фазы: input → animation → traversal → commit |
| VSync | метроном | аппаратный импульс дисплея; DispSync предсказывает и разносит фазы app/sf |
| Sync barrier (MQ) | сторож №1 | в MessageQueue блокирует посторонние сообщения между фазами кадра |
| Sync barrier (render) | сторож №2 | swap указателя staging→active; Main ждёт ФИКСАЦИЮ состояния, не отрисовку |
| BufferQueue | труба | dequeueBuffer → рисуем → queueBuffer(+fence) → SF: acquire → release |
| RenderThread | прораб | свой поток, проигрывает Display List через Skia → GL/Vulkan; GPU считает асинхронно |
| GPU | художник | тайлы, общая с CPU память; дороги overdraw и offscreen-проходы |
| SurfaceFlinger + HWC | оформитель | складывает слои всех окон; DEVICE = железо, CLIENT = GPU |

## Ключевые «взрослые» факты

- `draw()` **ничего не рисует** — записывает команды. «Тяжёлая» отрисовка в `onDraw` бьёт дважды.
- Main и RenderThread **параллельно**: пока RT рисует кадр N, Main строит N+1.
- Дерево RenderNode при sync **не копируется** — swap указателя. Долгий `syncFrameState` ≈ текстуры/слои, не «большой Display List».
- Fence: **acquire** (SF ждёт: «GPU ещё рисует»), **release** (мы ждём: «дисплей ещё показывает» = back-pressure), **present** (кадр на экране).
- **«RT спит» — чаще следствие**, чем причина: искать, что набило очередь.
- **Буферы покупают плавность за латентность** (2 vs 3). Buffer stuffing: пропусков нет, а «вязко» — очередь набита, каждый кадр ждёт лишний такт. Лечится удалением долгого кадра или frame pacing.
- Дорого не «нарисовать», а **перезаписать**: overdraw / saveLayer / blur едят пропускную способность общей RAM.
- HWC: **DEVICE** (оверлеи железом, почти бесплатно) vs **CLIENT** (GPU). DEVICE не всегда выгоден (простой с прозрачностью).
- `Modifier.alpha/offset(x=dp)` → рекомпозиция; `graphicsLayer { }` → только render property на RT.
- С Android 12: **BLASTBufferQueue** — очередь в процессе приложения, буфер + геометрия едут одной транзакцией.

## Диагностика джанка

Порядок: **Frame Timeline → чья сторона → дорожка виновного → одна гипотеза → одно изменение → перемерить.**

| Симптом | Этаж | Что делать |
|---|---|---|
| Долгий `traversal` на Main | дизайнер | лишняя работа в composition/measure (2.2) |
| Распухший `syncFrameState` | передача ТЗ | текстуры/картинки (2.4, 2.5) |
| Долгий `issue draw commands` | художник | overdraw, saveLayer, blur — или просто много команд |
| Долгий `dequeueBuffer` | стопка пуста | искать долгий кадр ДО него (2.8) |
| Пропусков нет, но «вязко» | очередь набита | buffer stuffing, frame pacing (2.8) |

Инструменты: Perfetto (`perfetto -t 10s sched gfx view am`), `dumpsys gfxinfo <pkg> framestats`, `debug.hwui.overdraw show`, Layout Inspector, JankStats.

## Одна фраза на всю тему

> Кадр делают четверо, параллельно и каждый своим делом — не чини остальных троих.
