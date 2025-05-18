#!/bin/bash
set -e

# Wait additional 30 seconds
echo "Database is healthy, waiting an extra 30 seconds..."
sleep 30

# Start the application
echo "Starting the application..."
exec java -jar /app/app.jar
