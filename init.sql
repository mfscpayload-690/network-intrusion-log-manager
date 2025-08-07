-- Initialize database for Network Intrusion Log Manager
USE network_intrusion_logs;

-- Create the intrusion_logs table
CREATE TABLE IF NOT EXISTS intrusion_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45),
    threat_type VARCHAR(100),
    severity VARCHAR(50),
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    log_message VARCHAR(255),
    log_level VARCHAR(50)
);

-- Insert some sample data
INSERT INTO intrusion_logs (ip_address, threat_type, severity, log_timestamp) VALUES
('192.168.1.100', 'DDoS', 'Critical', NOW() - INTERVAL 1 HOUR),
('10.0.0.50', 'Malware', 'High', NOW() - INTERVAL 2 HOUR),
('172.16.0.25', 'SQL Injection', 'High', NOW() - INTERVAL 3 HOUR),
('192.168.1.200', 'Unauthorized Access', 'Medium', NOW() - INTERVAL 4 HOUR),
('10.10.10.10', 'Phishing', 'Medium', NOW() - INTERVAL 5 HOUR),
('172.16.50.100', 'Bruteforce', 'High', NOW() - INTERVAL 6 HOUR),
('192.168.2.75', 'MITM', 'Critical', NOW() - INTERVAL 7 HOUR),
('10.0.1.150', 'DNS Spoofing', 'High', NOW() - INTERVAL 8 HOUR),
('172.20.0.30', 'Other', 'Low', NOW() - INTERVAL 9 HOUR),
('192.168.3.45', 'DDoS', 'Critical', NOW() - INTERVAL 10 HOUR);

-- Grant permissions to app user
GRANT ALL PRIVILEGES ON network_intrusion_logs.* TO 'appuser'@'%';
FLUSH PRIVILEGES;
