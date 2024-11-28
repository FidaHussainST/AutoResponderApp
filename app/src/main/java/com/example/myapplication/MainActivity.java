package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAPTURE = 1001;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    private static final String TAG = "MainActivity";

    // EditText for displaying logs (now scrollable and non-editable)
    private EditText logsEditText;
    private Button refresh;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the EditText for logs
        logsEditText = findViewById(R.id.logs_edit_text);
        refresh=findViewById(R.id.refresh_logs_button);

        // Set EditText to be non-editable and scrollable
        logsEditText.setKeyListener(null); // Make it non-editable
        logsEditText.setVerticalScrollBarEnabled(true); // Enable scrolling
        logsEditText.setHorizontalScrollBarEnabled(true); // Enable horizontal scrolling
       refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               updateLogsDisplay();
           }
       });


        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            LogManager.addLog("Start button clicked.");
            if (Settings.canDrawOverlays(this)) {
                LogManager.addLog("Overlay permission granted, starting screen capture.");
                startScreenCapture();
            } else {
                LogManager.addLog("Overlay permission not granted, requesting permission.");
                requestOverlayPermission();
            }
            updateLogsDisplay();
        });

        // Stop button click listener
        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {
            // Show a confirmation dialog
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Warning")
                    .setMessage("Do you really want to stop? This app will be terminated!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User confirmed the action
                        LogManager.addLog("User confirmed termination.");
                        stopAutoResponderService(); // Stop the service

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // User canceled the action
                        LogManager.addLog("User canceled termination.");
                        dialog.dismiss(); // Close the dialog
                    })
                    .setCancelable(true) // Allow the user to cancel by tapping outside the dialog
                    .show();
        });

    }

    private void startScreenCapture() {
        new Thread(() -> {
            try {
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                Intent captureIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, REQUEST_CODE_CAPTURE);
            } catch (Exception e) {
                LogManager.addLog("Error starting screen capture: " + e.getMessage());
                updateLogsDisplay();
            }
        }).start();
    }

    private void requestOverlayPermission() {
        Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivityForResult(overlayIntent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                LogManager.addLog("Overlay permission granted.");
                startScreenCapture();
            } else {
                LogManager.addLog("Overlay permission denied.");
            }
            updateLogsDisplay();
        }
        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
            Intent serviceIntent = new Intent(this, AutoResponderService.class);
            serviceIntent.putExtra("resultCode", resultCode);
            serviceIntent.putExtra("data", data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            LogManager.addLog("Screen capture started successfully.");
            updateLogsDisplay();
        }
    }

    // Stop AutoResponderService and cancel the notification
    private void stopAutoResponderService() {
        try {
            // Send a stop signal to the AutoResponderService
            Intent stopIntent = new Intent(this, AutoResponderService.class);
            stopIntent.putExtra("action", "stop");
            startService(stopIntent); // Notify the service to stop itself
            LogManager.addLog("Stop signal sent to AutoResponderService.");
        } catch (Exception e) {
            LogManager.addLog("Error sending stop signal: " + e.getMessage());
        }
    }




    // Method to update the EditText with logs
    private void updateLogsDisplay() {
        String logs = LogManager.getLogs(); // Fetch the logs from LogManager
        logsEditText.setText(logs); // Set the logs in the EditText
        logsEditText.setSelection(logsEditText.getText().length()); // Scroll to the bottom of the logs
    }

    // Method to reset the UI to its initial state (like when the app is newly opened)
    private void resetUI() {
        logsEditText.setText(""); // Clear the logs
        LogManager.addLog("UI reset to initial state.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Ensure that on back press, the service is stopped and UI is reset
        stopAutoResponderService();
        resetUI();
        updateLogsDisplay();
    }
}
