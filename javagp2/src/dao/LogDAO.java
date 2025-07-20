package dao;

import model.IntrusionLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {

    public boolean addLog(IntrusionLog log) {
        String sql = "INSERT INTO intrusion_logs (ip_address, threat_type, severity, log_timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, log.getIpAddress());
            stmt.setString(2, log.getThreatType());
            stmt.setString(3, log.getSeverity());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearAllLogs() {
        String sql = "DELETE FROM intrusion_logs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affectedRows = stmt.executeUpdate();
            return affectedRows >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertPreFedLogs(List<IntrusionLog> logs) {
        String sql = "INSERT INTO intrusion_logs (ip_address, threat_type, severity, log_timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (IntrusionLog log : logs) {
                stmt.setString(1, log.getIpAddress());
                stmt.setString(2, log.getThreatType());
                stmt.setString(3, log.getSeverity());
                stmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            for (int res : results) {
                if (res == PreparedStatement.EXECUTE_FAILED) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLog(int id) {
        String sql = "DELETE FROM intrusion_logs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<IntrusionLog> getFilteredLogs(String severityFilter, String threatTypeFilter) {
        List<IntrusionLog> logs = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, ip_address, threat_type, severity, log_timestamp FROM intrusion_logs WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (severityFilter != null && !severityFilter.equalsIgnoreCase("All")) {
            sqlBuilder.append(" AND severity = ?");
            params.add(severityFilter);
        }

        if (threatTypeFilter != null && !threatTypeFilter.equalsIgnoreCase("All")) {
            sqlBuilder.append(" AND threat_type = ?");
            params.add(threatTypeFilter);
        }

        sqlBuilder.append(" ORDER BY log_timestamp DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                IntrusionLog log = new IntrusionLog();
                log.setId(rs.getInt("id"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setThreatType(rs.getString("threat_type"));
                log.setSeverity(rs.getString("severity"));
                Timestamp ts = rs.getTimestamp("log_timestamp");
                if (ts != null) {
                    log.setTimestamp(ts.toLocalDateTime());
                }
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public List<IntrusionLog> getAllLogs() {
        return getFilteredLogs("All", "All");
    }
}
