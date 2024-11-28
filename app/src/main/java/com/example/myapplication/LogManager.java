package com.example.myapplication;


import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static final String TAG = "LogManager";
    private static final List<String> logList = new ArrayList<>();
    private static final int MAX_LOG_SIZE = 1000; // Limit to 1000 lines

    // Add log to the list
    public static void addLog(String log) {
        if (logList.size() >= MAX_LOG_SIZE) {
            logList.remove(0); // Remove the oldest log when max size is reached
        }
        logList.add(log);
        Log.d(TAG, log);  // Also log to Logcat for debugging purposes
    }

    // Get all logs as a single string for display
    public static String getLogs() {
        StringBuilder logs = new StringBuilder();
        for (String log : logList) {
            logs.append(log).append("\n");
        }
        return logs.toString();
    }
}
