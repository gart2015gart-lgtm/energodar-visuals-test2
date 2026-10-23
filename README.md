# Energodar Visuals (без ESP) (Fabric, Minecraft 1.21.11)

Клиентский визуал-мод. Меню — **Right Shift**, свап элитры — **V** (меняются в Управление → Energodar Visuals).

## Модули
- (версия без ESP)
- **Elytra Swap** — одна клавиша: нагрудник ⇄ элитра (лучший нагрудник выбирается автоматически).
- **Яркость** (fullbright), **HUD** (ватермарк, FPS/пинг, координаты, прочность брони, Target HUD), **эффекты удара** (частицы, звук).

Настройки хранятся в `config/energodar_visuals.json`.

## Сборка
Нужны JDK 21 и Gradle 9.5+ (или IntelliJ IDEA: File → Open → build.gradle).

    gradle build

Готовый мод: `build/libs/energodar-visuals-noesp-1.0.0.jar` → положить в `mods/` вместе с Fabric API 0.141.6+1.21.11 (или новее для 1.21.11).
Без установки: залить папку на GitHub — Actions (`.github/workflows/build.yml`) соберёт jar в Artifacts.
