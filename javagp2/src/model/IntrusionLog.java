package model;

public class IntrusionLog extends LogEntry {
    public IntrusionLog() {
        super();
    }

    public IntrusionLog(int id, String ipAddress, String threatType, String severity, java.time.LocalDateTime timestamp) {
        super(id, ipAddress, threatType, severity, timestamp);
    }
}
