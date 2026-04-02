import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeatherDashboard extends JFrame {

    private static final String API_KEY = "c0f08f86727caba23d4f3da8920f5965";
    private static final String API_KEY_BACKUP = "a37bf73e19a1828070c3497e370981f0";
    
    private Map<String, String> defaultCities;
    
    private JLabel tempLabel;
    private JLabel conditionLabel;
    private JLabel iconLabel;
    private JLabel dateLabel;
    private JLabel cityLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel visibilityLabel;
    private JTextField searchField;
    
    public WeatherDashboard() {
        setTitle("Next-Gen Weather Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initDefaultCities();
        
        // Main Container with Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        
        // Layout Config
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(createMainContent(), BorderLayout.CENTER);
        
        fetchWeatherData("London"); // Default fetch
    }
    
    private void initDefaultCities() {
        defaultCities = new HashMap<>();
        defaultCities.put("New York", "10001,US");
        defaultCities.put("London", "EC1A,GB");
        defaultCities.put("Mumbai", "400001,IN");
        defaultCities.put("Tokyo", "100-0001,JP");
        defaultCities.put("Sydney", "2000,AU");
    }

    private JPanel createSidebar() {
        GlassPanel sidebar = new GlassPanel(15);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quick Select");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebar.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (Map.Entry<String, String> entry : defaultCities.entrySet()) {
            JButton cityBtn = createCityButton(entry.getKey(), entry.getValue());
            listPanel.add(cityBtn);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        sidebar.add(scrollPane, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createCityButton(String cityName, String query) {
        JButton btn = new JButton(cityName) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 60));
                } else {
                    g2.setColor(new Color(255, 255, 255, 30));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> fetchWeatherData(query));
        
        return btn;
    }

    private JPanel createMainContent() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header Structure
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        dateLabel = new JLabel(getCurrentDate());
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        dateLabel.setForeground(new Color(220, 220, 220));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        searchField = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 150));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setOpaque(false);
        searchField.setBorder(new EmptyBorder(5, 15, 5, 15));
        searchField.addActionListener(e -> fetchWeatherData(searchField.getText()));

        JButton searchBtn = new JButton("Search\uD83D\uDD0D") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(new Color(255, 255, 255, 80));
                else g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> fetchWeatherData(searchField.getText()));

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        headerPanel.add(dateLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Center Area (Temperature and Condition)
        GlassPanel centerPanel = new GlassPanel(30);
        centerPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        cityLabel = new JLabel("Loading...");
        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        cityLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(cityLabel, gbc);
        
        iconLabel = new JLabel("\u23F3");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 80));
        iconLabel.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(iconLabel, gbc);
        
        tempLabel = new JLabel("--°C");
        tempLabel.setFont(new Font("SansSerif", Font.BOLD, 90));
        tempLabel.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(tempLabel, gbc);
        
        conditionLabel = new JLabel("Please wait...");
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        conditionLabel.setForeground(new Color(230, 230, 230));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(conditionLabel, gbc);

        // Bottom Area (Details)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomPanel.setOpaque(false);
        
        humidityLabel = createDetailCard(bottomPanel, "Humidity\uD83D\uDCA7", "--%");
        windLabel = createDetailCard(bottomPanel, "Wind\uD83D\uDCA8", "-- m/s");
        visibilityLabel = createDetailCard(bottomPanel, "Visibility\uD83D\uDC41\uFE0F", "-- km");

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Wrap center to provide margins
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(40, 0, 40, 0));
        centerWrapper.add(centerPanel, BorderLayout.CENTER);
        
        contentPanel.add(centerWrapper, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JLabel createDetailCard(JPanel parent, String titleText, String valText) {
        GlassPanel card = new GlassPanel(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));
        card.setPreferredSize(new Dimension(0, 120));
        
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.PLAIN, 16));
        title.setForeground(new Color(220, 220, 220));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel val = new JLabel(valText);
        val.setFont(new Font("SansSerif", Font.BOLD, 22));
        val.setForeground(Color.WHITE);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        val.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(val);
        card.add(Box.createVerticalGlue());
        
        parent.add(card);
        return val;
    }

    private void fetchWeatherData(String query) {
        if (query == null || query.trim().isEmpty()) return;
        
        cityLabel.setText("Loading...");
        
        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                String q = query.trim().replace(" ", "%20");
                URL url;
                // Check if it's a pincode (contains digits)
                if (q.matches(".*\\d.*") && q.contains(",")) {
                    url = new URL("https://api.openweathermap.org/data/2.5/weather?zip=" + q + "&appid=" + API_KEY + "&units=metric");
                } else if(q.matches("\\d+")){
                    // Assume Indian pincode for pure numbers just as an example, but better to enforce City,Country for zip
                    url = new URL("https://api.openweathermap.org/data/2.5/weather?zip=" + q + ",IN&appid=" + API_KEY + "&units=metric");
                } else {
                    url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + q + "&appid=" + API_KEY + "&units=metric");
                }
                
                String jsonResponse = makeApiRequest(url);
                if (jsonResponse == null) {
                    // Try backup API key
                    String backupUrlStr = url.toString().replace(API_KEY, API_KEY_BACKUP);
                    jsonResponse = makeApiRequest(new URL(backupUrlStr));
                }
                
                if (jsonResponse != null) {
                    return parseJson(jsonResponse);
                }
                throw new Exception("API Request failed");
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    updateUIWithData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    cityLabel.setText("Not Found");
                    tempLabel.setText("--°C");
                    conditionLabel.setText("Error fetching data");
                    iconLabel.setText("\u26A0\uFE0F");
                }
            }
        };
        worker.execute();
    }
    
    private String makeApiRequest(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private WeatherData parseJson(String json) {
        WeatherData data = new WeatherData();
        
        // Manual parsing to keep dependency-free
        data.cityName = extractString(json, "\"name\":\"", "\"");
        data.temp = extractDouble(json, "\"temp\":", ",");
        data.humidity = (int) extractDouble(json, "\"humidity\":", ",");
        data.windSpeed = extractDouble(json, "\"speed\":", ",");
        data.visibility = (int) extractDouble(json, "\"visibility\":", ",");
        data.description = extractString(json, "\"description\":\"", "\"");
        
        String conditionMain = extractString(json, "\"main\":\"", "\"");
        data.iconEmoji = getIconFromCondition(conditionMain);

        return data;
    }

    private String extractString(String json, String startKey, String endMarker) {
        int start = json.indexOf(startKey);
        if (start == -1) return "Unknown";
        start += startKey.length();
        int end = json.indexOf(endMarker, start);
        if(end == -1) end = json.indexOf("}", start); // fallback
        if(end == -1) return "Unknown";
        String val = json.substring(start, end).trim();
        return val.substring(0, 1).toUpperCase() + val.substring(1);
    }
    
    private double extractDouble(String json, String startKey, String endMarker) {
        int start = json.indexOf(startKey);
        if (start == -1) return 0.0;
        start += startKey.length();
        int end = json.indexOf(endMarker, start);
        if(end == -1) end = json.indexOf("}", start); // fallback
        if (end == -1) return 0.0;
        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String getIconFromCondition(String condition) {
        if (condition == null) return "\u2753"; // Question mark
        condition = condition.toLowerCase();
        if (condition.contains("clear")) return "\u2600\uFE0F"; // Sun
        if (condition.contains("cloud")) return "\u2601\uFE0F"; // Cloud
        if (condition.contains("rain")) return "\uD83C\uDF27\uFE0F"; // Rain
        if (condition.contains("snow")) return "\u2744\uFE0F"; // Snow
        if (condition.contains("thunderstorm")) return "\u26C8\uFE0F"; // Thunderstorm
        if (condition.contains("drizzle")) return "\uD83C\uDF26\uFE0F"; // Sun behind rain cloud
        if (condition.contains("mist") || condition.contains("haze") || condition.contains("fog")) return "\uD83C\uDF2B\uFE0F"; // Fog
        return "\uD83C\uDF25\uFE0F"; // Sun behind cloud
    }

    private void updateUIWithData(WeatherData data) {
        cityLabel.setText(data.cityName);
        tempLabel.setText(Math.round(data.temp) + "°C");
        conditionLabel.setText(data.description);
        humidityLabel.setText(data.humidity + "%");
        windLabel.setText(data.windSpeed + " m/s");
        visibilityLabel.setText((data.visibility / 1000.0) + " km");
        iconLabel.setText(data.iconEmoji);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        return sdf.format(new Date());
    }

    // --- Custom Components & Models ---

    private static class WeatherData {
        String cityName;
        double temp;
        int humidity;
        double windSpeed;
        int visibility;
        String description;
        String iconEmoji;
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Sleek deep-blue-to-purple gradient
            Color color1 = new Color(15, 32, 39);
            Color color2 = new Color(32, 58, 67);
            Color color3 = new Color(44, 83, 100);
            
            LinearGradientPaint lgp = new LinearGradientPaint(
                0, 0, 0, getHeight(),
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{color1, color2, color3}
            );
            
            g2d.setPaint(lgp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    private class GlassPanel extends JPanel {
        private int cornerRadius;

        public GlassPanel(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Semi-transparent fill
            g2d.setColor(new Color(255, 255, 255, 25));
            RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2d.fill(rect);

            // White border
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(rect);
            
            g2d.dispose();
            super.paintComponent(g);
        }
    }
    
    private class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(255, 255, 255, 50);
            this.trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // Run gracefully on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Ignore failure, fall back to default
            }
            new WeatherDashboard().setVisible(true);
        });
    }
}
