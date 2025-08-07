FROM openjdk:21-jdk-slim

# Install necessary packages for GUI and MySQL client
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libasound2 \
    libfreetype6 \
    libfontconfig1 \
    default-mysql-client \
    netcat-traditional \
    x11-utils \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy application files
COPY javagp2/ ./javagp2/
COPY run.sh ./
COPY docker-entrypoint.sh ./

# Ensure bin directory exists and compile Java application
RUN mkdir -p javagp2/bin && \
    find javagp2/src -name "*.java" | xargs javac \
    -cp "javagp2/lib/mysql-connector-j-9.3.0.jar:javagp2/lib/jackson-core-2.15.2.jar:javagp2/lib/jackson-databind-2.15.2.jar:javagp2/lib/jackson-annotations-2.15.2.jar:javagp2/lib/jackson-datatype-jsr310-2.15.2.jar" \
    -d javagp2/bin && \
    mkdir -p javagp2/bin/dao && \
    cp javagp2/resources/db.properties javagp2/bin/dao/

# Make scripts executable
RUN chmod +x run.sh docker-entrypoint.sh

# Set environment variables for display (for GUI)
ENV DISPLAY=:0
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=false"

# Create a user for running the application
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
RUN chown -R appuser:appgroup /app

# Expose port for any future web interface (optional)
EXPOSE 8080

# Entry point script
ENTRYPOINT ["./docker-entrypoint.sh"]
