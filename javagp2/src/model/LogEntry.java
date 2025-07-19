package model;

import java.time.LocalDateTime;

public abstract class LogEntry {
    private int id;
    private String ipAddress;
    private String threatType;
    private String severity;
    private LocalDateTime timestamp;

    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }

    public LogEntry(int id, String ipAddress, String threatType, String severity, LocalDateTime timestamp) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.threatType = threatType;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getThreatType() {
        return threatType;
    }

    public void setThreatType(String threatType) {
        this.threatType = threatType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
