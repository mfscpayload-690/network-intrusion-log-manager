#!/bin/bash

echo "🛡️ Network Intrusion Log Manager - Docker Container"
echo "🐳 Starting containerized application..."

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL database to be ready..."
while ! nc -z mysql 3306; do
  echo "Waiting for MySQL..."
  sleep 2
done
echo "✅ Database is ready!"

# Update database configuration for container environment
echo "🔧 Updating database configuration..."
cat > javagp2/bin/dao/db.properties << EOF
# Database connection properties for containerized environment
db.url=jdbc:mysql://mysql:3306/network_intrusion_logs
db.user=appuser
db.password=apppassword
EOF

cat > javagp2/resources/db.properties << EOF
# Database connection properties for containerized environment
db.url=jdbc:mysql://mysql:3306/network_intrusion_logs
db.user=appuser
db.password=apppassword
EOF

# Fix X11 permissions and test connection
echo "🔧 Setting up X11 display..."
echo "DISPLAY is set to: $DISPLAY"
ls -la /tmp/.X11-unix/ || echo "X11 socket directory not found"

# Test X11 connection
echo "🧪 Testing X11 connection..."
if command -v xset &> /dev/null; then
    xset q &> /dev/null && echo "✅ X11 connection working" || echo "❌ X11 connection failed"
fi

# Start the Java application
echo "🚀 Launching Network Intrusion Log Manager..."
exec java -cp javagp2/bin:javagp2/lib/mysql-connector-j-9.3.0.jar:javagp2/lib/jackson-core-2.15.2.jar:javagp2/lib/jackson-databind-2.15.2.jar:javagp2/lib/jackson-annotations-2.15.2.jar:javagp2/lib/jackson-datatype-jsr310-2.15.2.jar:javagp2/resources App
