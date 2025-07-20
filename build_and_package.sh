#!/bin/bash

# Variables
SRC_DIR="javagp2/src"
BIN_DIR="javagp2/bin"
MYSQL_CONNECTOR_JAR="javagp2/mysql-connector-j-9.3.0.jar"
MAIN_CLASS="ui.MainFrame"
JAR_NAME="NetworkIntrusionLogManager.jar"

# Clean previous bin directory
rm -rf $BIN_DIR
mkdir -p $BIN_DIR

# Compile source files
javac -cp "$MYSQL_CONNECTOR_JAR" -d $BIN_DIR $(find $SRC_DIR -name "*.java")

# Copy MySQL connector jar to bin directory
cp "$MYSQL_CONNECTOR_JAR" $BIN_DIR/

# Create manifest file inside bin directory
echo "Main-Class: $MAIN_CLASS" > $BIN_DIR/manifest.txt
echo "Class-Path: mysql-connector-j-9.3.0.jar" >> $BIN_DIR/manifest.txt

# Create executable jar
cd $BIN_DIR
echo "Files in bin directory before jar command:"
ls -l
jar_output=$(jar cfm $JAR_NAME manifest.txt $(find . -type f) 2>&1)
echo "Jar command output:"
echo "$jar_output"
echo "Files in bin directory after jar command:"
ls -l

# Move jar to project root if it exists
if [ -f "$JAR_NAME" ]; then
  mv $JAR_NAME ../
else
  echo "JAR file $JAR_NAME not found in $BIN_DIR"
fi

# Cleanup
cd ..
if [ -f "$BIN_DIR/manifest.txt" ]; then
  rm $BIN_DIR/manifest.txt
else
  echo "Manifest file not found for cleanup"
fi

echo "Build and packaging complete. Run the application with:"
echo "java -jar $JAR_NAME"
