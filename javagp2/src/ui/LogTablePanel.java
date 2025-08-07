package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LogTablePanel extends JPanel {
    private JTable logTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;

    public LogTablePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 24, 28));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,255,128)),
                "Intrusion Logs", 0, 0, new Font("JetBrains Mono", Font.BOLD, 16), new Color(0,255,128)));

        String[] columns = {"Log ID", "IP Address", "Threat Type", "Severity", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable cells
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    return getValueAt(0, columnIndex).getClass();
                }
                return super.getColumnClass(columnIndex);
            }
        };
        logTable = new JTable(tableModel);
        logTable.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        logTable.setForeground(new Color(0,255,128));
        logTable.setBackground(new Color(30,34,40));
        logTable.setGridColor(new Color(0,255,128));
        logTable.setSelectionBackground(new Color(0,64,32));
        logTable.setSelectionForeground(Color.WHITE);
        logTable.setRowHeight(28);
        logTable.getTableHeader().setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        logTable.getTableHeader().setBackground(new Color(20,24,28));
        logTable.getTableHeader().setForeground(new Color(0,255,128));

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 24, 28));

        deleteButton = createStyledButton("🗑️ Delete Selected Log", new Color(220, 53, 69), new Color(255, 80, 80));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedLog();
            }
        });
        
        // Add refresh button
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(40, 167, 69), new Color(60, 200, 90));
        refreshButton.addActionListener(e -> refreshTable());
        
        // Add Generate Sample Data button
        JButton generateButton = createStyledButton("🔥 Generate Sample Data", new Color(255, 193, 7), new Color(255, 235, 59));
        generateButton.addActionListener(e -> generateSampleData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(generateButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from database in background - don't block UI
        SwingUtilities.invokeLater(() -> refreshTableAsync());
    }

    public void refreshTable() {
        refreshTableWithFilter("All", "All");
    }

    public void refreshTableWithFilter(String severity, String threatType) {
        tableModel.setRowCount(0); // Clear existing data
        LogDAO dao = new LogDAO();
        List<IntrusionLog> logs = dao.getFilteredLogs(severity, threatType);
        for (IntrusionLog log : logs) {
            Object[] row = {
                String.format("%04d", log.getId()),
                log.getIpAddress(),
                log.getThreatType(),
                log.getSeverity(),
                log.getTimestamp() != null ? log.getTimestamp().toString() : ""
            };
            tableModel.addRow(row);
        }
    }

    private JButton createStyledButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Simplified hover effect without intensive timers
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(normalColor.darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
        
        return button;
    }

    private void deleteSelectedLog() {
        int selectedRow = logTable.getSelectedRow();
        if (selectedRow == -1) {
            // Create custom styled message dialog
            showStyledMessage("⚠️ No Selection", "Please select a log to delete.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Fix: Handle both String and Integer types for log ID
            Object logIdObj = tableModel.getValueAt(selectedRow, 0);
            int logId;
            if (logIdObj instanceof String) {
                // Remove any formatting (like leading zeros) and parse
                logId = Integer.parseInt(((String) logIdObj).replaceAll("^0+", ""));
            } else {
                logId = (Integer) logIdObj;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete log ID: " + logId + "?", 
                "🗑️ Confirm Delete", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                LogDAO dao = new LogDAO();
                boolean success = dao.deleteLog(logId);
                if (success) {
                    showStyledMessage("✅ Success", "Log deleted successfully.", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                } else {
                    showStyledMessage("❌ Error", "Failed to delete log. Please try again.", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            showStyledMessage("❌ Error", "Invalid log ID format.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showStyledMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // Background refresh to prevent UI freezing
    private void refreshTableAsync() {
        SwingWorker<List<IntrusionLog>, Void> worker = new SwingWorker<List<IntrusionLog>, Void>() {
            @Override
            protected List<IntrusionLog> doInBackground() throws Exception {
                try {
                    LogDAO dao = new LogDAO();
                    return dao.getFilteredLogs("All", "All");
                } catch (Exception e) {
                    System.err.println("Database error: " + e.getMessage());
                    return new java.util.ArrayList<>(); // Return empty list on error
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<IntrusionLog> logs = get();
                    tableModel.setRowCount(0); // Clear existing data
                    
                    if (logs.isEmpty()) {
                        // Show message that no data is available
                        Object[] row = {"--", "No data available", "Click 'Generate Sample Data'", "to get started", "--"};
                        tableModel.addRow(row);
                    } else {
                        for (IntrusionLog log : logs) {
                            Object[] row = {
                                String.format("%04d", log.getId()),
                                log.getIpAddress(),
                                log.getThreatType(),
                                log.getSeverity(),
                                log.getTimestamp() != null ? log.getTimestamp().toString() : ""
                            };
                            tableModel.addRow(row);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error updating table: " + e.getMessage());
                    // Show error message in table
                    tableModel.setRowCount(0);
                    Object[] row = {"ERROR", "Database connection failed", "Check connection", "and try again", "--"};
                    tableModel.addRow(row);
                }
            }
        };
        worker.execute();
    }
    
    // Generate sample data for testing
    private void generateSampleData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    LogDAO dao = new LogDAO();
                    List<IntrusionLog> sampleLogs = new java.util.ArrayList<>();
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    
                    String[] threatTypes = {"Unauthorized Access", "DDoS", "Malware", "Phishing", "Bruteforce", "SQL Injection", "MITM", "DNS Spoofing"};
                    String[] severities = {"Low", "Medium", "High", "Critical"};
                    String[] ipPrefixes = {"192.168.1.", "10.0.0.", "172.16.0.", "203.0.113."};
                    
                    for (int i = 1; i <= 50; i++) {
                        IntrusionLog log = new IntrusionLog();
                        log.setIpAddress(ipPrefixes[i % ipPrefixes.length] + (i % 254 + 1));
                        log.setThreatType(threatTypes[i % threatTypes.length]);
                        log.setSeverity(severities[i % severities.length]);
                        log.setTimestamp(now.minusHours(i).minusMinutes(i * 3));
                        sampleLogs.add(log);
                    }
                    
                    dao.insertPreFedLogs(sampleLogs);
                } catch (Exception e) {
                    System.err.println("Error generating sample data: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    showStyledMessage("✅ Success", "Sample data generated successfully!", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableAsync(); // Refresh the table to show new data
                });
            }
        };
        worker.execute();
    }
}
