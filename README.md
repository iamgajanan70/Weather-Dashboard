# 🌤️ Next-Gen Weather Dashboard

A beautifully designed, modern weather application built entirely in Java (Swing/AWT) from the ground up natively without external visual libraries. It pulls real-time weather data using the OpenWeatherMap API and boasts a stunning visual experience utilizing **Glassmorphism**, smooth gradients, and anti-aliased typography.

## ✨ Features
- **Sleek Glassmorphism UI:** Semi-transparent panels, elegant white borders, and soft shadows provide a "super cool" modern look.
- **Real-Time Weather Data:** Fetches active temperature, humidity, wind speed, visibility, and weather conditions.
- **Dynamic Icons:** Intuitive emoji-based icons to quickly denote active weather (e.g. 🌧️, ☀️, ❄️).
- **Responsive Layout:** Engineered with `GridBagLayout` and `BorderLayout` to provide spacious padding and responsiveness.
- **Smart Location Search:** Supports standard city searches and postal/pin code integration.
- **Quick Select Sidebar:** Easy access to a scrollable preset list of major cities natively integrated without UI freezes.
- **Asynchronous Execution:** Handles heavy API requests safely off the Event Dispatch Thread (EDT) utilizing `SwingWorker` for zero UI locking.
- **Zero External Dependencies:** Built solely with `javax.swing.*`, `java.awt.*`, and standard Java networking/I/O classes.

## 🛠️ Technology Stack
- **Language:** Java (JDK 23+)
- **Frontend GUI:** Java Swing & AWT (`Graphics2D`)
- **Backend/API:** `java.net.HttpURLConnection`
- **Data Source:** [OpenWeatherMap API](https://openweathermap.org/)
- **Data Parsing:** Native manual JSON string parsing

## 🚀 Quick Start & Installation

### Prerequisites
- Formally tested on **Java JDK 8 through JDK 23+**. Ensure Java is installed and available in your system path.

### 1. Clone the repository
To get a local copy up and running, execute the following command in your terminal:

```bash
git clone https://github.com/iamGajanan70/Weather-Dashboard.git
cd next-gen-weather-dashboard
```

*(Note: Replace `iamGajanan` and the repository URL with the actual GitHub link if operating under a different fork).*

### 2. Enter your API Key (Optional)
The application comes internally pre-configured with a default active API Key. However, for continuous production usage, update the `API_KEY` static variables inside `WeatherDashboard.java` with your own free OpenWeather key.
```java
private static final String API_KEY = "your_primary_api_key_here";
```

### 3. Compile the Code
Compile the Java file utilizing the Java compiler:
```bash
javac WeatherDashboard.java
```

### 4. Run the Application
Execute the compiled Java bytecode class:
```bash
java WeatherDashboard
```

## 🎮 Usage
- **Search functionality:** Enter a city name (e.g., *Paris*) or a pin code into the top right search bar and press **Search 🔍**.
- **Sidebar presets:** Simply click on any major city populated in the *Quick Select* sidebar.
- **Details:** The bottom trio-panel instantly displays specific localized data surrounding Humidity 💧, Wind Speed 💨, and Visibility 👁️. 

## 💬 Code Quality Notes
- `Graphics2D` handles high-quality rendering utilizing explicit `RenderingHints.KEY_ANTIALIASING` enabled preventing jagged edges.
- The `GlassPanel` and `GradientPanel` inner classes override `paintComponent()` for optimized direct rendering of aesthetics. 

---
*Developed as a robust, dependency-free Java graphics exhibit.* 💻☕
