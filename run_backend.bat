@echo off
echo ===============================================================================
echo BULACAN STATE UNIVERSITY - EQUIPMENT BORROWING SYSTEM
echo Spring Boot Backend REST API Launcher (Port 8080)
echo ===============================================================================

SET BACKEND_JAR=%~dp0backend\target\equipment-borrowing-backend-1.0.0.jar

IF NOT EXIST "%BACKEND_JAR%" (
    echo [INFO] JAR file not found. Building with Maven...
    cd /d "%~dp0backend"
    call mvn clean package -DskipTests
    cd /d "%~dp0"
)

echo [INFO] Starting Spring Boot REST API...
java -jar "%BACKEND_JAR%"

pause
