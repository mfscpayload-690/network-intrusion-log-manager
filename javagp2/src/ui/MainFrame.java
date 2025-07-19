 package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel navPanel;
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("Network Intrusion Log Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set dark theme and monospace font
        setUIFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        getContentPane().setBackground(new Color(20, 24, 28));

        // Navigation panel (left)
        navPanel = new JPanel();
        navPanel.setBackground(new Color(30, 34, 40));
        navPanel.setPreferredSize(new Dimension(200, 0)); // Increased width
        navPanel.setMinimumSize(new Dimension(200, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("\uD83D\uDEE1️ Intrusion Logs");
        logo.setForeground(new Color(0, 255, 128));
        logo.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(Box.createVerticalStrut(30));
        navPanel.add(logo);
        navPanel.add(Box.createVerticalStrut(40));

        JButton addLogBtn = createNavButton("Add Log");
        JButton viewLogsBtn = createNavButton("View Logs");
        JButton filterBtn = createNavButton("Filter");

        navPanel.add(addLogBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(viewLogsBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(filterBtn);
        navPanel.add(Box.createVerticalGlue());

        // Content panel (center) with CardLayout
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(20, 24, 28));

        // Panels for each section
        LogTablePanel logTablePanel = new LogTablePanel();
        LogFormPanel logFormPanel = new LogFormPanel(logTablePanel);
        JPanel filterPanel = createFilterPanel();

JLabel welcomeLabel = new JLabel("Welcome to Network Intrusion Log Manager!", JLabel.CENTER);
welcomeLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 28));
welcomeLabel.setForeground(new Color(0, 255, 128));
welcomeLabel.setOpaque(true);
welcomeLabel.setBackground(new Color(20, 24, 28));
welcomeLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 128), 2));
welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);

contentPanel.add(welcomeLabel, "HOME");

// Animation: simple color pulse effect
new javax.swing.Timer(100, e -> {
    float[] hsb = Color.RGBtoHSB(0, 255, 128, null);
    float brightness = (float) ((Math.sin(System.currentTimeMillis() / 500.0) + 1) / 2 * 0.5 + 0.5);
    Color animatedColor = Color.getHSBColor(hsb[0], hsb[1], brightness);
    welcomeLabel.setForeground(animatedColor);
}).start();
        contentPanel.add(logFormPanel, "FORM");
        contentPanel.add(logTablePanel, "TABLE");
        contentPanel.add(filterPanel, "FILTER");

        // Button actions to switch cards
        addLogBtn.addActionListener(e -> showCard("FORM"));
        viewLogsBtn.addActionListener(e -> {
            LogDAO dao = new LogDAO();
            if (dao.getAllLogs().isEmpty()) {
                // Insert 50 pre-fed logs
                List<IntrusionLog> preFedLogs = new java.util.ArrayList<>();
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                String[] threatTypes = {"Unauthorized Access", "DDoS", "Malware", "Phishing", "Other", "Bruteforce", "SQL Injection", "MITM", "DNS Spoofing"};
                String[] severities = {"Low", "Medium", "High", "Critical"};
                for (int i = 1; i <= 50; i++) {
                    IntrusionLog log = new IntrusionLog();
                    log.setIpAddress("192.168.1." + i);
                    log.setThreatType(threatTypes[i % threatTypes.length]);
                    log.setSeverity(severities[i % severities.length]);
                    log.setTimestamp(now.minusDays(i));
                    preFedLogs.add(log);
                }
                dao.clearAllLogs();
                dao.insertPreFedLogs(preFedLogs);
            }
            showCard("TABLE");
        });
        filterBtn.addActionListener(e -> showCard("FILTER"));

        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

        private JPanel createFilterPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(20, 24, 28));
            panel.setLayout(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,255,128)),
                    "Filter Logs", 0, 0, new Font("JetBrains Mono", Font.BOLD, 16), new Color(0,255,128)));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel severityLabel = new JLabel("Severity:");
            severityLabel.setForeground(new Color(0,255,128));
            severityLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(severityLabel, gbc);

            JComboBox<String> severityBox = new JComboBox<>(new String[] {"All", "Low", "Medium", "High", "Critical"});
            severityBox.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
            severityBox.setBackground(new Color(30,34,40));
            severityBox.setForeground(Color.WHITE);
            gbc.gridx = 1;
            panel.add(severityBox, gbc);

            JLabel threatLabel = new JLabel("Threat Type:");
            threatLabel.setForeground(new Color(0,255,128));
            threatLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(threatLabel, gbc);

            JComboBox<String> threatBox = new JComboBox<>(new String[] {"All", "Unauthorized Access", "DDoS", "Malware", "Phishing", "Other", "Bruteforce", "SQL Injection", "MITM", "DNS Spoofing"});
            threatBox.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
            threatBox.setBackground(new Color(30,34,40));
            threatBox.setForeground(Color.WHITE);
            gbc.gridx = 1;
            panel.add(threatBox, gbc);

            JButton applyBtn = new JButton("Apply Filter");
            applyBtn.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
            applyBtn.setBackground(new Color(0,255,128));
            applyBtn.setForeground(Color.BLACK);
            applyBtn.setFocusPainted(false);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            panel.add(applyBtn, gbc);

            JLabel info = new JLabel("(Filtering is UI-only for now)");
            info.setFont(new Font("JetBrains Mono", Font.ITALIC, 12));
            info.setForeground(new Color(0,255,128));
            gbc.gridy = 3;
            panel.add(info, gbc);

            return panel;
        }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setMinimumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        btn.setBackground(new Color(0, 255, 128));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // Utility to set default font for all UI
    public static void setUIFont(Font f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, f);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
