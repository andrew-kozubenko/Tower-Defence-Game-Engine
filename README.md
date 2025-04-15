# Tower-Defence-Game-Engine
Repository for the Software Design project.

## Режим разработки (Dev Mode)

**Что делает:**  
При `devMode=true` (настройка в `application.properties`), приложение копирует файлы из папки `TowerDefenceSDExample` в `Documents/Games/TowerDefenceSD`, **если** таких файлов там нет. Это упрощает совместную разработку и тестирование:  
- Эталонные конфигурации (карты, башни и пр.) лежат в репозитории в `TowerDefenceSDExample`.  
- При запуске в dev-режиме эти файлы автоматом попадают в локальную папку пользователя.  

**Как включить:**  
1. Открыть `application.properties` в `src/main/resources`.  
2. Поставить `devMode=true`.  
3. Запустить приложение.  

Пример лога копирования:

[DEV] devMode = true -> начинаем копирование файлов.

**Отключение:**  
- Поменять `devMode=true` на `devMode=false`. Тогда файлы **не** копируются, а используются уже существующие.