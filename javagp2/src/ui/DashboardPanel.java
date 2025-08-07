package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.TimerTask;

// Jackson imports - only used when Jackson JAR is available
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DashboardPanel extends JPanel {
    private JTextArea realTimeLogArea;
    private JLabel totalLogsLabel;
    private JLabel criticalAlertsLabel;
    private JLabel unresolvedThreatsLabel;
    private JPanel chartsPanel;
    private JPanel alertsPanel;
    private JPanel filterPanel;
    private JPanel userProfilePanel;
    private JPanel networkMapPanel;
    private JButton exportButton;
    private JButton importButton;

    private LogDAO logDAO;
    private java.util.Timer refreshTimer;
    private MainFrame mainFrame;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        logDAO = new LogDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(20, 24, 28));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(new Color(20, 24, 28));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 255, 128)),
                "Summary Statistics", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("JetBrains Mono", Font.BOLD, 16), new Color(0, 255, 128)));

        totalLogsLabel = createSummaryLabel("Total Logs", "0");
        criticalAlertsLabel = createSummaryLabel("Critical Alerts", "0");
        unresolvedThreatsLabel = createSummaryLabel("Unresolved Threats", "0");

        summaryPanel.add(createSummaryPanel("Total Logs", totalLogsLabel));
        summaryPanel.add(createSummaryPanel("Critical Alerts", criticalAlertsLabel));
        summaryPanel.add(createSummaryPanel("Unresolved Threats", unresolvedThreatsLabel));

        add(summaryPanel, BorderLayout.NORTH);

        // Center panel with real-time logs and charts
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(new Color(20, 24, 28));

        // Real-time log feed panel
        JPanel realTimePanel = new JPanel(new BorderLayout());
        realTimePanel.setBackground(new Color(20, 24, 28));
        realTimePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 255, 128)),
                "Real-Time Log Feed", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("JetBrains Mono", Font.BOLD, 16), new Color(0, 255, 128)));

        realTimeLogArea = new JTextArea();
        realTimeLogArea.setEditable(false);
        realTimeLogArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        realTimeLogArea.setBackground(new Color(30, 34, 40));
        realTimeLogArea.setForeground(new Color(0, 255, 128));
        realTimeLogArea.setLineWrap(true);
        realTimeLogArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(realTimeLogArea);
        realTimePanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(realTimePanel);

        // Charts panel with simple bar chart for threat types
        // Removed charts panel as per user request
//        chartsPanel = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                paintCharts(g);
//            }
//        };
//        chartsPanel.setBackground(new Color(20, 24, 28));
//        chartsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 255, 128)),
//                "Charts & Statistics", TitledBorder.LEFT, TitledBorder.TOP,
//                new Font("JetBrains Mono", Font.BOLD, 16), new Color(0, 255, 128)));
//        centerPanel.add(chartsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Adjust preferred size to avoid overlap with bottom panel
        centerPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 300));

        // Bottom panel with alerts, filters, user profile, network map, export/import
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        bottomPanel.setBackground(new Color(20, 24, 28));

        alertsPanel = createPlaceholderPanel("Alerts & Notifications");
        filterPanel = createPlaceholderPanel("Advanced Filters");
        userProfilePanel = createPlaceholderPanel("User Profile / Settings");
        networkMapPanel = createPlaceholderPanel("Network Topology / Map");

        exportButton = new JButton("Export Logs");
        exportButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        exportButton.setBackground(new Color(0, 255, 128));
        exportButton.setForeground(Color.BLACK);

        importButton = new JButton("Import Logs");
        importButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        importButton.setBackground(new Color(0, 255, 128));
        importButton.setForeground(Color.BLACK);

        bottomPanel.add(alertsPanel);
        bottomPanel.add(filterPanel);
        bottomPanel.add(userProfilePanel);
        bottomPanel.add(networkMapPanel);

        JPanel importExportPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        importExportPanel.setBackground(new Color(20, 24, 28));
        importExportPanel.add(exportButton);
        importExportPanel.add(importButton);

        bottomPanel.add(importExportPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Start timer to refresh real-time logs and summary stats every 3 seconds
        refreshTimer = new java.util.Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshDashboard());
            }
        }, 0, 3000);

        // Add action listeners for export and import buttons
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportLogs();
            }
        });

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importLogs();
            }
        });
    }

    private void paintCharts(Graphics g) {
        List<IntrusionLog> logs = logDAO.getAllLogs();

        // Calculate threat type counts
        Map<String, Long> threatTypeCounts = logs.stream()
                .collect(Collectors.groupingBy(IntrusionLog::getThreatType, Collectors.counting()));

        // Calculate severity counts
        Map<String, Long> severityCounts = logs.stream()
                .collect(Collectors.groupingBy(IntrusionLog::getSeverity, Collectors.counting()));

        int width = getWidth();
        int height = getHeight();

        int padding = 30;
        int chartWidth = (width - 3 * padding) / 2;
        int chartHeight = height - 3 * padding;

        // Draw threat type bar chart
        int x = padding;
        int y = padding;

        g.setColor(new Color(0, 255, 128));
        g.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        g.drawString("Threat Types", x, y - 15);

        int maxCount = threatTypeCounts.values().stream().mapToInt(Long::intValue).max().orElse(1);
        int barWidth = chartWidth / Math.max(threatTypeCounts.size(), 1);

        int barX = x;
        for (Map.Entry<String, Long> entry : threatTypeCounts.entrySet()) {
            int barHeight = (int) ((entry.getValue() * chartHeight) / maxCount);
            g.fillRect(barX, y + chartHeight - barHeight, barWidth - 5, barHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("JetBrains Mono", Font.PLAIN, 10));
            g.drawString(entry.getKey(), barX, y + chartHeight + 15);
            g.setColor(new Color(0, 255, 128));
            barX += barWidth;
        }

        // Draw severity bar chart
        x = padding * 2 + chartWidth;
        g.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        g.drawString("Severity", x, y - 15);

        maxCount = severityCounts.values().stream().mapToInt(Long::intValue).max().orElse(1);
        barWidth = chartWidth / Math.max(severityCounts.size(), 1);

        barX = x;
        for (Map.Entry<String, Long> entry : severityCounts.entrySet()) {
            int barHeight = (int) ((entry.getValue() * chartHeight) / maxCount);
            g.fillRect(barX, y + chartHeight - barHeight, barWidth - 5, barHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("JetBrains Mono", Font.PLAIN, 10));
            g.drawString(entry.getKey(), barX, y + chartHeight + 15);
            g.setColor(new Color(0, 255, 128));
            barX += barWidth;
        }
    }

    private void exportLogs() {
        List<IntrusionLog> logs = logDAO.getAllLogs();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Logs");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON and CSV files", "json", "csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getName().toLowerCase();
            try {
                if (fileName.endsWith(".json")) {
                    exportLogsToJson(logs, fileToSave);
                } else if (fileName.endsWith(".csv")) {
                    exportLogsToCsv(logs, fileToSave);
                } else {
                    JOptionPane.showMessageDialog(this, "Please specify a file with .json or .csv extension", "Invalid file extension", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting logs: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportLogsToJson(List<IntrusionLog> logs, File file) throws IOException {
        // Simple JSON export without Jackson - basic format
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("[\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < logs.size(); i++) {
                IntrusionLog log = logs.get(i);
                writer.write("  {\n");
                writer.write(String.format("    \"id\": %d,\n", log.getId()));
                writer.write(String.format("    \"ipAddress\": \"%s\",\n", log.getIpAddress()));
                writer.write(String.format("    \"threatType\": \"%s\",\n", log.getThreatType()));
                writer.write(String.format("    \"severity\": \"%s\",\n", log.getSeverity()));
                writer.write(String.format("    \"timestamp\": \"%s\"\n", log.getTimestamp().format(formatter)));
                writer.write("  }");
                if (i < logs.size() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("]\n");
        }
    }

    private void exportLogsToCsv(List<IntrusionLog> logs, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,ipAddress,threatType,severity,timestamp\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (IntrusionLog log : logs) {
                writer.write(String.format("%d,%s,%s,%s,%s\n",
                        log.getId(),
                        log.getIpAddress(),
                        log.getThreatType(),
                        log.getSeverity(),
                        log.getTimestamp().format(formatter)));
            }
        }
    }

    private void importLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Logs");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON and CSV files", "json", "csv"));
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            String fileName = fileToOpen.getName().toLowerCase();
            try {
                List<IntrusionLog> importedLogs;
                if (fileName.endsWith(".json")) {
                    importedLogs = importLogsFromJson(fileToOpen);
                } else if (fileName.endsWith(".csv")) {
                    importedLogs = importLogsFromCsv(fileToOpen);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a file with .json or .csv extension", "Invalid file extension", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (importedLogs != null && !importedLogs.isEmpty()) {
                    boolean success = logDAO.insertPreFedLogs(importedLogs);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Logs imported successfully", "Import Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshDashboard();
                        if (mainFrame != null) {
                            mainFrame.refreshLogTable();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to import logs", "Import Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing logs: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<IntrusionLog> importLogsFromJson(File file) throws IOException {
        // Simple JSON parsing - expects format created by exportLogsToJson
        List<IntrusionLog> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String content = reader.lines().reduce("", String::concat);
            // Basic JSON parsing for our specific format
            if (content.contains("[") && content.contains("]")) {
                content = content.substring(content.indexOf('[') + 1, content.lastIndexOf(']'));
                String[] objects = content.split("\\},\\s*\\{");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                
                for (String obj : objects) {
                    if (!obj.trim().isEmpty()) {
                        IntrusionLog log = new IntrusionLog();
                        
                        // Extract values using basic string parsing
                        String idStr = extractValue(obj, "id");
                        String ipStr = extractValue(obj, "ipAddress");
                        String threatStr = extractValue(obj, "threatType");
                        String severityStr = extractValue(obj, "severity");
                        String timestampStr = extractValue(obj, "timestamp");
                        
                        if (!idStr.isEmpty()) log.setId(Integer.parseInt(idStr));
                        if (!ipStr.isEmpty()) log.setIpAddress(ipStr);
                        if (!threatStr.isEmpty()) log.setThreatType(threatStr);
                        if (!severityStr.isEmpty()) log.setSeverity(severityStr);
                        if (!timestampStr.isEmpty()) {
                            log.setTimestamp(java.time.LocalDateTime.parse(timestampStr, formatter));
                        }
                        
                        logs.add(log);
                    }
                }
            }
        }
        return logs;
    }
    
    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            // String value
            start++; // Skip opening quote
            int end = json.indexOf('"', start);
            return end > start ? json.substring(start, end) : "";
        } else {
            // Number value
            int end = start;
            while (end < json.length() && Character.isDigit(json.charAt(end))) {
                end++;
            }
            return end > start ? json.substring(start, end) : "";
        }
    }

    private List<IntrusionLog> importLogsFromCsv(File file) throws IOException {
        List<IntrusionLog> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    IntrusionLog log = new IntrusionLog();
                    log.setId(Integer.parseInt(parts[0]));
                    log.setIpAddress(parts[1]);
                    log.setThreatType(parts[2]);
                    log.setSeverity(parts[3]);
                    log.setTimestamp(java.time.LocalDateTime.parse(parts[4], formatter));
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    private JLabel createSummaryLabel(String title, String value) {
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 255, 128));
        return valueLabel;
    }

    private JPanel createSummaryPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 24, 28));
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 255, 128));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 24, 28));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 255, 128)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("JetBrains Mono", Font.BOLD, 14), new Color(0, 255, 128)));
        JLabel label = new JLabel("Feature coming soon...", JLabel.CENTER);
        label.setFont(new Font("JetBrains Mono", Font.ITALIC, 12));
        label.setForeground(new Color(0, 255, 128));
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void refreshDashboard() {
        List<IntrusionLog> logs = logDAO.getAllLogs();

        // Update summary stats
        int totalLogs = logs.size();
        int criticalAlerts = (int) logs.stream().filter(log -> "Critical".equalsIgnoreCase(log.getSeverity())).count();
        // For demo, unresolved threats count as critical alerts count (can be improved)
        int unresolvedThreats = criticalAlerts;

        updateSummaryLabel(totalLogsLabel, totalLogs);
        updateSummaryLabel(criticalAlertsLabel, criticalAlerts);
        updateSummaryLabel(unresolvedThreatsLabel, unresolvedThreats);

        // Update real-time log feed (show last 10 logs)
        List<IntrusionLog> recentLogs = logs.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(10)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (IntrusionLog log : recentLogs) {
            sb.append(String.format("[%s] %s - %s - %s\n",
                    log.getTimestamp().format(formatter),
                    log.getIpAddress(),
                    log.getThreatType(),
                    log.getSeverity()));
        }
        realTimeLogArea.setText(sb.toString());
    }

    private void updateSummaryLabel(JLabel label, int value) {
        label.setText(String.valueOf(value));
    }

    public void stopRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}
