package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class LogFormPanel extends JPanel {
    private JTextField ipAddressField;
    private JComboBox<String> threatTypeComboBox;
    private JComboBox<String> severityComboBox;
    private JButton addButton;
    private LogTablePanel logTablePanel; // Reference to LogTablePanel for live update

    public LogFormPanel(LogTablePanel logTablePanel) {
        this.logTablePanel = logTablePanel;

        setBackground(new Color(20, 24, 28));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,255,128)),
                    "Add Log", 0, 0, new Font("JetBrains Mono", Font.BOLD, 16), new Color(0,255,128)));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel ipLabel = new JLabel("IP Address:");
        ipLabel.setForeground(new Color(0, 255, 128));
        ipLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(ipLabel, gbc);

        ipAddressField = new JTextField(15);
        ipAddressField.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipAddressField, gbc);

        JLabel threatLabel = new JLabel("Threat Type:");
        threatLabel.setForeground(new Color(0, 255, 128));
        threatLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(threatLabel, gbc);

        threatTypeComboBox = new JComboBox<>(new String[] {"Unauthorized Access", "DDoS", "Malware", "Phishing", "Bruteforce", "SQL Injection", "MITM", "DNS Spoofing", "Other"});
        threatTypeComboBox.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        threatTypeComboBox.setBackground(new Color(30, 34, 40));
        threatTypeComboBox.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(threatTypeComboBox, gbc);

        JLabel severityLabel = new JLabel("Severity:");
        severityLabel.setForeground(new Color(0, 255, 128));
        severityLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(severityLabel, gbc);

        severityComboBox = new JComboBox<>(new String[] {"Low", "Medium", "High", "Critical"});
        severityComboBox.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        severityComboBox.setBackground(new Color(30, 34, 40));
        severityComboBox.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(severityComboBox, gbc);

        addButton = new JButton("✅ Add Log");
        addButton.setBackground(new Color(0, 255, 128));
        addButton.setForeground(Color.BLACK);
        addButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add hover effect
        addHoverEffect(addButton, new Color(0, 255, 128), new Color(0, 220, 100));
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addButton, gbc);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLog();
            }
        });
    }
    
    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer hoverTimer;
            private Color currentColor = normalColor;
            private boolean isHovering = false;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovering = true;
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(15, evt -> {
                    int r = currentColor.getRed();
                    int g = currentColor.getGreen();
                    int b = currentColor.getBlue();
                    
                    int targetR = hoverColor.getRed();
                    int targetG = hoverColor.getGreen();
                    int targetB = hoverColor.getBlue();
                    
                    if (Math.abs(r - targetR) <= 3 && Math.abs(g - targetG) <= 3 && Math.abs(b - targetB) <= 3) {
                        currentColor = hoverColor;
                        button.setBackground(currentColor);
                        hoverTimer.stop();
                        return;
                    }
                    
                    r += (targetR - r) > 0 ? Math.max(1, (targetR - r) / 6) : Math.min(-1, (targetR - r) / 6);
                    g += (targetG - g) > 0 ? Math.max(1, (targetG - g) / 6) : Math.min(-1, (targetG - g) / 6);
                    b += (targetB - b) > 0 ? Math.max(1, (targetB - b) / 6) : Math.min(-1, (targetB - b) / 6);
                    
                    currentColor = new Color(Math.max(0, Math.min(255, r)), 
                                           Math.max(0, Math.min(255, g)), 
                                           Math.max(0, Math.min(255, b)));
                    button.setBackground(currentColor);
                });
                hoverTimer.start();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovering = false;
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(15, evt -> {
                    int r = currentColor.getRed();
                    int g = currentColor.getGreen();
                    int b = currentColor.getBlue();
                    
                    int targetR = normalColor.getRed();
                    int targetG = normalColor.getGreen();
                    int targetB = normalColor.getBlue();
                    
                    if (Math.abs(r - targetR) <= 3 && Math.abs(g - targetG) <= 3 && Math.abs(b - targetB) <= 3) {
                        currentColor = normalColor;
                        button.setBackground(currentColor);
                        hoverTimer.stop();
                        return;
                    }
                    
                    r += (targetR - r) > 0 ? Math.max(1, (targetR - r) / 6) : Math.min(-1, (targetR - r) / 6);
                    g += (targetG - g) > 0 ? Math.max(1, (targetG - g) / 6) : Math.min(-1, (targetG - g) / 6);
                    b += (targetB - b) > 0 ? Math.max(1, (targetB - b) / 6) : Math.min(-1, (targetB - b) / 6);
                    
                    currentColor = new Color(Math.max(0, Math.min(255, r)), 
                                           Math.max(0, Math.min(255, g)), 
                                           Math.max(0, Math.min(255, b)));
                    button.setBackground(currentColor);
                });
                hoverTimer.start();
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor.darker());
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(isHovering ? hoverColor : normalColor);
            }
        });
    }

    private void addLog() {
        String ipAddress = ipAddressField.getText().trim();
        String threatType = (String) threatTypeComboBox.getSelectedItem();
        String severity = (String) severityComboBox.getSelectedItem();

        if (ipAddress.isEmpty() || threatType == null || severity == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        IntrusionLog log = new IntrusionLog();
        log.setIpAddress(ipAddress);
        log.setThreatType(threatType);
        log.setSeverity(severity);
        log.setTimestamp(LocalDateTime.now());

        LogDAO dao = new LogDAO();
        boolean success = dao.addLog(log);
        if (success) {
            JOptionPane.showMessageDialog(this, "Log added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            ipAddressField.setText("");
            threatTypeComboBox.setSelectedIndex(0);
            severityComboBox.setSelectedIndex(0);
            if (logTablePanel != null) {
                logTablePanel.refreshTable(); // Refresh logs table immediately
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add log.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
