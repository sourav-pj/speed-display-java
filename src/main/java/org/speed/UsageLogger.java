package org.speed;

import java.io.*;
import java.time.LocalDate;

public class UsageLogger {
    // This will create the log in C:\Users\YourName\network_usage_log.csv on Windows
    private static final String LOG_FILE = System.getProperty("user.home") + File.separator + "network_usage_log.csv";
    private long dailyRx = 0;
    private long dailyTx = 0;
    private LocalDate currentDay = LocalDate.now();

    public void addUsage(long rx, long tx) {
        checkRollover();
        if (rx > 0) dailyRx += rx;
        if (tx > 0) dailyTx += tx;
    }

    private void checkRollover() {
        if (LocalDate.now().isAfter(currentDay)) {
            saveToDisk();
            dailyRx = 0;
            dailyTx = 0;
            currentDay = LocalDate.now();
        }
    }

    public void saveToDisk() {
        File file = new File(LOG_FILE);
        boolean exists = file.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            if (!exists) pw.println("Date,Down_Bytes,Up_Bytes,Down_Human,Up_Human");
            pw.printf("%s,%d,%d,%s,%s%n", currentDay, dailyRx, dailyTx,
                    NetworkModel.formatSpeed(dailyRx), NetworkModel.formatSpeed(dailyTx));
        } catch (IOException e) {
            System.err.println("Logging failed: File locked.");
        }
    }

    public void loadTodayStats() {
        File file = new File(LOG_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(currentDay.toString())) {
                    String[] p = line.split(",");
                    dailyRx = Long.parseLong(p[1]);
                    dailyTx = Long.parseLong(p[2]);
                }
            }
        } catch (Exception ignored) {}
    }

    public void resetToday() {
        dailyRx = 0;
        dailyTx = 0;
        saveToDisk();
    }

    public long getDailyRx() { return dailyRx; }
    public long getDailyTx() { return dailyTx; }
    public LocalDate getCurrentDay() { return currentDay; }
}