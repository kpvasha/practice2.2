import utils.FilesystemUtilities;

import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class DiaryApplications {
    private static final int MAX_ENTRIES = 50;
    private String[] dates = new String[MAX_ENTRIES];
    private String[] entries = new String[MAX_ENTRIES];
    private long[] timestamps = new long[MAX_ENTRIES];
    private int entryCount = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private String currentFilePath = "";
    private boolean isModified = false;

    public void run(Scanner scanner) {
        chooseDateFormat(scanner);
        loadOrCreateDiary(scanner);

        while (true) {
            System.out.println("""
                \nМій щоденник:
                1. Додати запис
                2. Видалити запис
                3. Переглянути записи
                4. Зберегти щоденник
                5. Змінити формат дати
                6. Вийти    
                    """);
            System.out.print("Оберіть: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addEntry(scanner);
                    break;
                case "2":
                    deleteEntry(scanner);
                    break;
                case "3":
                    viewAllEntries();
                    break;
                case "4":
                    saveDiary(scanner);
                    break;
                case "5":
                    chooseDateFormat(scanner);
                    break;
                case "6":
                    exitProgram(scanner);
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void chooseDateFormat(Scanner scanner) {
        System.out.println("\nОберіть формат відображення дати:");
        System.out.println("1. dd.MM.yyyy HH:mm (25.12.2024 14:30)");
        System.out.println("2. yyyy-MM-dd HH:mm (2024-12-25 14:30)");
        System.out.println("3. dd/MM/yyyy HH:mm (25/12/2024 14:30)");
        System.out.println("4. MM-dd-yyyy HH:mm (12-25-2024 14:30)");
        System.out.println("5. Власний формат");
        System.out.print("Ваш вибір: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                break;
            case "2":
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case "3":
                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                break;
            case "4":
                dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                break;
            case "5":
                System.out.print("Введіть власний формат (наприклад, dd.MM.yyyy HH:mm): ");
                String customFormat = scanner.nextLine();
                try {
                    dateFormat = new SimpleDateFormat(customFormat);
                    dateFormat.format(new Date());
                } catch (Exception e) {
                    System.out.println("Невірний формат! Використовується стандартний.");
                    dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                }
                break;
            default:
                System.out.println("Невірний вибір! Використовується стандартний формат.");
                dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        }

        System.out.println("Встановлений формат: " + dateFormat.toPattern());
    }

    private void loadOrCreateDiary(Scanner scanner) {
        System.out.print("Завантажити існуючий щоденник? (y/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y") || choice.equals("yes") || choice.equals("так")) {
            System.out.print("Введіть шлях до файлу щоденника: ");
            String filePath = scanner.nextLine();

            FilesystemUtilities.DiaryData data = FilesystemUtilities.loadDiary(filePath);
            if (data != null) {
                this.dates = data.dates;
                this.entries = data.entries;
                this.timestamps = data.timestamps;
                this.entryCount = data.entryCount;
                this.dateFormat = data.dateFormat;
                this.currentFilePath = filePath;
                this.isModified = false;

                System.out.println("Щоденник успішно завантажено!");
                System.out.println("Завантажено записів: " + entryCount);
            } else {
                System.out.println("Не вдалося завантажити щоденник. Створюється новий.");
            }
        } else {
            System.out.println("Створюється новий щоденник.");
        }
    }

    private void addEntry(Scanner scanner) {
        if (entryCount >= MAX_ENTRIES) {
            System.out.println("Видаліть якісь записи, щоденник повний");
            return;
        }

        System.out.println("1. Поточна дата та час");
        System.out.println("2. Ввести дату та час вручну");
        System.out.print("Оберіть: ");
        String dateChoice = scanner.nextLine();

        Date entryDate;

        if (dateChoice.equals("1")) {
            entryDate = new Date();
        } else {
            System.out.print("Введіть дату та час (" + dateFormat.toPattern() + "): ");
            String dateInput = scanner.nextLine();

            try {
                entryDate = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Невірний формат дати!");
                return;
            }
        }

        System.out.println("Введіть текст запису (для завершення введіть символ '/'):");

        String entry = "";
        String line;
        while (true) {
            line = scanner.nextLine();
            if (line.equals("/")) {
                break;
            }
            entry += line + "\n";
        }

        if (!entry.isEmpty()) {
            dates[entryCount] = dateFormat.format(entryDate);
            entries[entryCount] = entry;
            timestamps[entryCount] = entryDate.getTime();
            entryCount++;
            isModified = true;
            System.out.println("Запис додано");
        }
    }

    private void deleteEntry(Scanner scanner) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній");
            return;
        }

        System.out.print("Введіть дату запису для видалення (" + dateFormat.toPattern() + "): ");
        String dateToDelete = scanner.nextLine();

        for (int i = 0; i < entryCount; i++) {
            if (dates[i].equals(dateToDelete)) {
                for (int j = i; j < entryCount - 1; j++) {
                    dates[j] = dates[j + 1];
                    entries[j] = entries[j + 1];
                    timestamps[j] = timestamps[j + 1];
                }
                entryCount--;
                isModified = true;
                System.out.println("Запис видалено");
                return;
            }
        }
        System.out.println("Запис з такою датою відсутній");
    }

    private void viewAllEntries() {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній");
            return;
        }

        System.out.println("\nВсі записи:");
        for (int i = 0; i < entryCount; i++) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Дата: " + dates[i]);
            System.out.println("Запис:");
            System.out.println(entries[i]);
        }
        System.out.println("=".repeat(50));
    }

    private boolean saveDiary(Scanner scanner) {
        String filePath = currentFilePath;

        if (filePath.isEmpty()) {
            System.out.print("Введіть шлях для збереження файлу: ");
            filePath = scanner.nextLine();
        }

        boolean success = FilesystemUtilities.saveDiary(filePath, dates, entries, timestamps, entryCount, dateFormat);
        if (success) {
            currentFilePath = filePath;
            isModified = false;
        }
        return success;
    }

    private void exitProgram(Scanner scanner) {
        if (isModified && entryCount > 0) {
            System.out.print("Є незбережені зміни. Зберегти щоденник перед виходом? (y/n): ");
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("y") || choice.equals("yes") || choice.equals("так")) {
                saveDiary(scanner);
            }
        }

        System.out.println("До побачення!");
    }
}