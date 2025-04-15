package ru.nsu.t4werok.towerdefence.dev;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Утилитный класс, который в dev-режиме копирует
 * файлы из TowerDefenceSDExample -> Documents/Games/TowerDefenceSD
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
            Files.createDirectories(DOCS_BASE_PATH);

            // Обходим всё дерево TowerDefenceSDExample
            Files.walkFileTree(DEV_EXAMPLE_PATH, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relative = DEV_EXAMPLE_PATH.relativize(dir);
                    Path targetDir = DOCS_BASE_PATH.resolve(relative);
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relative = DEV_EXAMPLE_PATH.relativize(file);
                    Path targetFile = DOCS_BASE_PATH.resolve(relative);
                    if (Files.notExists(targetFile)) {
                        Files.createDirectories(targetFile.getParent());
                        Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
                        System.out.println("[DEV] Скопировали: " + file + " -> " + targetFile);
                    } else {
                        // Если нужно, можно залогировать пропуск:
                        // System.out.println("[DEV] Пропуск, файл уже существует: " + targetFile);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("[DEV] Ошибка при копировании dev-файлов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
