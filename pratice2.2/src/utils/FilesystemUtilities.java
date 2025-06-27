package utils;

import java.io.*;
import java.text.SimpleDateFormat;

public class FilesystemUtilities {

    public static boolean saveDiary(String filePath, String[] dates, String[] entries,
                                    long[] timestamps, int entryCount, SimpleDateFormat dateFormat) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній, немає що зберігати");
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("DIARY_FORMAT_VERSION:1.0");
            writer.println("DATE_FORMAT:" + dateFormat.toPattern());
            writer.println("ENTRY_COUNT:" + entryCount);
            writer.println("---ENTRIES---");

            for (int i = 0; i < entryCount; i++) {
                writer.println("TIMESTAMP:" + timestamps[i]);
                writer.println("DATE:" + dates[i]);
                writer.println("ENTRY_START");
                writer.print(entries[i]);
                writer.println("ENTRY_END");
                writer.println();
            }

            System.out.println("Щоденник збережено у файл: " + filePath);
            return true;

        } catch (IOException e) {
            System.out.println("Помилка збереження файлу: " + e.getMessage());
            return false;
        }
    }

    public static utils.FilesystemUtilities.DiaryData loadDiary(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            line = reader.readLine();
            if (line == null || !line.startsWith("DIARY_FORMAT_VERSION:")) {
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            line = reader.readLine();
            if (line != null && line.startsWith("DATE_FORMAT:")) {
                String format = line.substring("DATE_FORMAT:".length());
                try {
                    dateFormat = new SimpleDateFormat(format);
                } catch (Exception e) {
                    dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                }
            }

            line = reader.readLine();
            if (line == null || !line.startsWith("ENTRY_COUNT:")) {
                return null;
            }

            line = reader.readLine();
            if (line == null || !line.equals("---ENTRIES---")) {
                return null;
            }

            String[] dates = new String[50];
            String[] entries = new String[50];
            long[] timestamps = new long[50];
            int entryCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TIMESTAMP:")) {
                    if (entryCount >= 50) break;

                    long timestamp = Long.parseLong(line.substring("TIMESTAMP:".length()));
                    timestamps[entryCount] = timestamp;

                    line = reader.readLine();
                    if (line != null && line.startsWith("DATE:")) {
                        dates[entryCount] = line.substring("DATE:".length());
                    }

                    line = reader.readLine();
                    if (line != null && line.equals("ENTRY_START")) {
                        String entry = "";
                        while ((line = reader.readLine()) != null) {
                            if (line.equals("ENTRY_END")) {
                                break;
                            }
                            entry += line + "\n";
                        }
                        entries[entryCount] = entry;
                        entryCount++;
                    }
                }
            }

            return new utils.FilesystemUtilities.DiaryData(dates, entries, timestamps, entryCount, dateFormat);

        } catch (IOException | NumberFormatException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
            return null;
        }
    }

    public static class DiaryData {
        public final String[] dates;
        public final String[] entries;
        public final long[] timestamps;
        public final int entryCount;
        public final SimpleDateFormat dateFormat;

        public DiaryData(String[] dates, String[] entries, long[] timestamps,
                         int entryCount, SimpleDateFormat dateFormat) {
            this.dates = dates;
            this.entries = entries;
            this.timestamps = timestamps;
            this.entryCount = entryCount;
            this.dateFormat = dateFormat;
        }
    }
}