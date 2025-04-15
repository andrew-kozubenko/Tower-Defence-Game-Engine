package ru.nsu.t4werok.towerdefence.dev;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Утилитный класс, который в dev-режиме копирует
 * файлы из TowerDefenceSDExample -> Documents/Games/TowerDefenceSD,
 * перезаписывая имеющиеся файлы.
 */
public class DevFileInitializer {

    private static final Path DEV_EXAMPLE_PATH = Paths.get("TowerDefenceSDExample");
    private static final Path DOCS_BASE_PATH = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "Games",
            "TowerDefenceSD"
    );

    public static void copyDevFilesIfNeeded(boolean devMode) {
        if (!devMode) {
            // Если devMode=false, ничего не копируем
            return;
        }
        System.out.println("[DEV] devMode = true -> начинаем копирование файлов.");

        try {
            if (Files.notExists(DEV_EXAMPLE_PATH)) {
                System.out.println("[DEV] Папка TowerDefenceSDExample не найдена: " + DEV_EXAMPLE_PATH);
                return;
            }
            // Создаём папку <user_home>/Documents/Games/TowerDefenceSD, если её нет
            Files.createDirectories(DOCS_BASE_PATH);

            // Рекурсивно обходим TowerDefenceSDExample
            Files.walkFileTree(DEV_EXAMPLE_PATH, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Создаём директории в целевом месте
                    Path relative = DEV_EXAMPLE_PATH.relativize(dir);
                    Path targetDir = DOCS_BASE_PATH.resolve(relative);
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Для каждого файла: строим путь к файлу в папке Documents...
                    Path relative = DEV_EXAMPLE_PATH.relativize(file);
                    Path targetFile = DOCS_BASE_PATH.resolve(relative);

                    // Создаём промежуточные директории (если вдруг не создались)
                    Files.createDirectories(targetFile.getParent());

                    // Перезаписываем файл при наличии (REPLACE_EXISTING), сохраняя атрибуты (COPY_ATTRIBUTES)
                    Files.copy(file, targetFile,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES);

                    System.out.println("[DEV] Скопировали (заменили): " + file + " -> " + targetFile);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("[DEV] Ошибка при копировании dev-файлов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
