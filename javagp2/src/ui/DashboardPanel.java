package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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
    private Timer refreshTimer;

    public DashboardPanel() {
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

        // Charts panel placeholder
        chartsPanel = new JPanel();
        chartsPanel.setBackground(new Color(20, 24, 28));
        chartsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 255, 128)),
                "Charts & Statistics (Coming Soon)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("JetBrains Mono", Font.BOLD, 16), new Color(0, 255, 128)));
        centerPanel.add(chartsPanel);

        add(centerPanel, BorderLayout.CENTER);

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
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshDashboard());
            }
        }, 0, 3000);
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
