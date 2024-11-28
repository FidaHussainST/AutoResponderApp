
# **Auto Responder App**

![Project Banner](https://github.com/FidaHussainST/AutoResponderApp/blob/master/github-header-image.png)  
*An Android application for logging, monitoring, and managing automated typing notifications and logs.*

---

## **Table of Contents**

1. [Introduction](#introduction)  
2. [Features](#features)  
3. [Screenshots](#screenshots)  
4. [Installation Guide](#installation-guide)  
5. [Project Structure](#project-structure)  
6. [Code Highlights](#code-highlights)  
7. [How to Use](#how-to-use)  
8. [Contributing](#contributing)  
9. [License](#license)  
10. [Contact](#contact)  

---

## **Introduction**

The **Auto Responder App** is a lightweight Android application designed for automation enthusiasts and developers. The app provides real-time logging of activity, sends notifications for detected actions, and offers controls for starting and stopping logs.  

The app's key use case includes:
- Monitoring "typing" notifications in real time.
- Logging actions with timestamps.
- Providing a seamless UI to view logs and manage processes.

---

## **Features**

- **Real-Time Notifications**: Detect typing actions and notify users.  
- **Custom Notification Sounds**: Supports personalized notification tones for better alerts.  
- **Logs Management**: View and manage logs in a scrollable interface.  
- **Start/Stop Actions**: Easily control the flow of actions with responsive buttons.  
- **Theme Support**: Elegant UI design with dark and light themes.  

---

## **Screenshots**

| Feature | Screenshot |
|---------|------------|
| **Home Screen** | ![Home Screen](https://github.com/FidaHussainST/AutoResponderApp/blob/master/app/src/main/res/drawable/Dashboard.jpg) |
| **Logs View** | ![Logs View](https://github.com/FidaHussainST/AutoResponderApp/blob/master/app/src/main/res/drawable/Logs.jpg) |
| **Notification** | ![Notification](https://github.com/FidaHussainST/AutoResponderApp/blob/master/app/src/main/res/drawable/Notification.jpg) |

---

## **Installation Guide**

### **Prerequisites**
- Android Studio (latest version recommended)  
- JDK 8+  
- A physical or virtual Android device (API Level 21 or above)  

### **Steps**
1. Clone the repository:  
   ```bash
   git clone https://github.com/FidaHussainST/AutoResponderApp.git
   cd auto-responder-app
   ```

2. Open the project in Android Studio.  

3. Sync Gradle to download dependencies.  

4. Run the app on an emulator or connected device.  

5. Test features such as notifications and logs.  

---

## **Project Structure**

```plaintext
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/app/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── NotificationHelper.java
│   │   │   │   ├── LogManager.java
│   │   │   │   ├── SettingsActivity.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── logs_view.xml
│   │   │   │   ├── raw/
│   │   │   │   │   ├── notification_sound.mp3
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   ├── build.gradle
│   ├── README.md
```

---

## **Code Highlights**

### Notification Integration

The app triggers notifications when typing is detected. Here’s a snippet demonstrating how to implement a custom sound:  

```java
Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "typing_channel")
        .setSmallIcon(R.drawable.icon)
        .setContentTitle("Typing Detected")
        .setContentText("User is typing...")
        .setSound(soundUri)
        .setPriority(NotificationCompat.PRIORITY_HIGH);
```

### Logs Management  

Logs are stored and displayed in a non-editable `EditText`.  
```xml
<EditText
    android:id="@+id/logs_edit_text"
    android:layout_width="match_parent"
    android:layout_height="500dp"
    android:focusable="false"
    android:cursorVisible="false"
    android:scrollbars="vertical"
    android:text="Logs will appear here..." />
```

---

## **How to Use**

1. **Start Logging**:  
   Tap the **Start** button to begin logging detected events.  

2. **View Logs**:  
   Scroll through the log section to review recent activities.  

3. **Receive Notifications**:  
   Notifications are automatically triggered during typing events.  

4. **Custom Notification Sound**:  
   Update the `notification_sound.mp3` file in the `res/raw` folder to use your custom sound.  

---

## **Contributing**

We welcome contributions! Follow these steps to contribute:  

1. Fork the repository.  
2. Create a new branch:  
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:  
   ```bash
   git commit -m "Added feature-name"
   ```
4. Push your branch and open a pull request.  

---

## **License**

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## **Contact**

**Author**: Fida Hussain
**My Personal Website**: [FidaHussain.me](https://fidahussain.me)
**Email**: fidahk786@gmail.com 
**GitHub**: [FidaHussainST](https://github.com/FidaHussainST)
