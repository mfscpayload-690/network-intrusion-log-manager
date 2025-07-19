package ui;

import dao.LogDAO;
import model.IntrusionLog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        deleteButton = new JButton("Delete Selected Log");
        deleteButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        deleteButton.setBackground(new Color(255, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedLog();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data from database
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        LogDAO dao = new LogDAO();
        List<IntrusionLog> logs = dao.getAllLogs();
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

    private void deleteSelectedLog() {
        int selectedRow = logTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a log to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int logId = Integer.parseInt((String) tableModel.getValueAt(selectedRow, 0));
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected log?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            LogDAO dao = new LogDAO();
            boolean success = dao.deleteLog(logId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Log deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete log.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
