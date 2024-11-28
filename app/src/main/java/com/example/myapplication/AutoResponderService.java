package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoResponderService extends AccessibilityService {

    private static final String TAG = "AutoResponderServices";
    private static final String TARGET_TEXT = "Price: RM"; // Target message text
    private static final double MINIMUM_PRICE = 85.0; // Minimum price to trigger reply
    private Set<Double> respondedPrices = new HashSet<>(); // Track responded prices
    private boolean serviceRunning = true; // Flag for service state
    private int typingCount = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created.");
        LogManager.addLog("Service created.");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!serviceRunning) {
            Log.d(TAG, "Service is stopped, ignoring events.");
            return;
        }
        if (event == null || event.getSource() == null) {
            Log.d(TAG, "Null event or source.");
            LogManager.addLog("Null event or source.");
            return;
        }

        if ("com.whatsapp".equals(event.getPackageName())) {
            Log.d(TAG, "Event from WhatsApp detected.");
            LogManager.addLog("Event from WhatsApp detected.");
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                processNodeTree(rootNode);
            } else {
                Log.d(TAG, "Root node is null.");
                LogManager.addLog("Root node is null.");
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted.");
        LogManager.addLog("Service interrupted.");
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra("action");
            if ("stop".equals(action)) {
                Log.d(TAG, "Stop action received. Stopping service and terminating app.");
                LogManager.addLog("Stop action received. Stopping service and terminating app.");

                // Cancel the notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel(1); // Cancel notification with ID 1
                }

                // Stop the service
                stopSelf();

                // Terminate the app
                terminateApp();
                return START_NOT_STICKY;
            }
        }

        // Start the service as a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, createNotification());
        }
        Log.d(TAG, "Service started.");
        LogManager.addLog("Service started.");
        return START_STICKY;
    }

    // Method to terminate the app
    private void terminateApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotification() {
        String channelId = "auto_responder_channel";
        String channelName = "Auto Responder Notifications";

        // Create the notification channel
        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Notifications for AutoResponderService");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        // Add intent to stop the service
        Intent stopIntent = new Intent(this, AutoResponderService.class);
        stopIntent.putExtra("action", "stop");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        return new Notification.Builder(this, channelId)
                .setContentTitle("AutoResponder Service")
                .setContentText("Monitoring messages. Tap Stop to disable.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(false) // Persistent notification unless stopped
                .addAction(
                        new Notification.Action.Builder(
                                null, // No action icon
                                "Stop", // Action text
                                stopPendingIntent // Pending intent for the Stop action
                        ).build()
                )
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceRunning = false;
        Log.d(TAG, "Service stopped.");
        LogManager.addLog("Service stopped.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
    }

    private void processNodeTree(AccessibilityNodeInfo node) {
        if (node == null || !serviceRunning) return;

        if ("android.widget.TextView".equals(node.getClassName()) && node.getText() != null) {
            String nodeText = node.getText().toString();

        if(nodeText.equals("M Ai is typing…")){

            String channelId = "typing_notification_channel";
            String channelName = "Typing Notifications";

// Create the notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                // Set the custom notification sound
                Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
                notificationChannel.setSound(customSoundUri, null);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }

// Build and trigger the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("M Ai is typing...")
                    .setContentText("M Ai has typed")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound)); // Custom sound for older Android versions

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(0, builder.build());
            }

// Cancel the notification after 10 seconds
            new Handler().postDelayed(() -> {
                if (notificationManager != null) {
                    notificationManager.cancel(0);
                }
            }, 10000);











        }
















            String regex = "Price: RM \\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(nodeText);

            if (matcher.find()) {
                double price = extractPrice(matcher.group());
                if (price >= MINIMUM_PRICE && !respondedPrices.contains(price)) {
                    // Perform swipe gesture first

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        int middleYValue = displayMetrics.heightPixels / 2;
                        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
                        final int rightSizeOfScreen = leftSideOfScreen * 3;

                        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                        Path path = new Path();

                        // Swipe left to right
                        path.moveTo(leftSideOfScreen, middleYValue);
                        path.lineTo(rightSizeOfScreen, middleYValue);

                        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 100, 50));
                        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                            @Override
                            public void onCompleted(GestureDescription gestureDescription) {

                                super.onCompleted(gestureDescription);

                                // Delay before sending the reply (in milliseconds)
                                new Handler(getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (price >= MINIMUM_PRICE && !respondedPrices.contains(price)) {

                                            sendReply("1", price);
                                        }
                                        // Send reply after the swipe

                                        LogManager.addLog( "Price detected and replied to: RM " + price);

                                        Log.d(TAG, "Price detected and replied to: RM " + price);
                                    }
                                }, 100); // 1 second delay (you can adjust this time as needed)
                            }
                        }, null);
                    }












                } else {
                    LogManager.addLog( "Price below threshold or already replied to: RM " + price);


                    Log.d(TAG, "Price below threshold or already replied to: RM " + price);
                }
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            processNodeTree(node.getChild(i));
        }
    }


    private double extractPrice(String priceText) {
        try {
            String[] parts = priceText.split("RM");
            if (parts.length > 1) {
                return Double.parseDouble(parts[1].trim());
            }
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error parsing price: " + e.getMessage());
            LogManager.addLog("Error parsing price: " + e.getMessage());
        }
        return 0.0;
    }

    private void sendReply(String replyMessage, double price) {
        if (!serviceRunning) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            Log.d(TAG, "Root node is null, cannot send reply.");
            LogManager.addLog( "Root node is null, cannot send reply.");
            return;
        }

        AccessibilityNodeInfo editTextNode = findNodeByClassName(rootNode, "android.widget.EditText");
        if (editTextNode != null) {
            editTextNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, replyMessage);
            editTextNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);

            AccessibilityNodeInfo sendButton = findNodeByDescription(rootNode, "Send");
            if (sendButton != null) {
                sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(TAG, "Reply sent successfully: " + replyMessage);
                LogManager.addLog("Reply sent successfully: " + replyMessage);
                respondedPrices.add(price);

            } else {
                Log.d(TAG, "Send button not found. Retrying later.");
                LogManager.addLog( "Send button not found. Retrying later.");
                respondedPrices.remove(price);
                processNodeTree(rootNode);
            }
        } else {
            Log.d(TAG, "EditText node not found, cannot send reply.");
            LogManager.addLog("EditText node not found, cannot send reply.");
        }
    }

    private AccessibilityNodeInfo findNodeByClassName(AccessibilityNodeInfo rootNode, String className) {
        if (rootNode == null) return null;

        if (className.equals(rootNode.getClassName())) {
            return rootNode;
        }

        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByClassName(rootNode.getChild(i), className);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findNodeByDescription(AccessibilityNodeInfo rootNode, String description) {
        if (rootNode == null) return null;

        if (description.equals(rootNode.getContentDescription())) {
            return rootNode;
        }

        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByDescription(rootNode.getChild(i), description);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
