@echo off
echo ===============================================================================
echo BULACAN STATE UNIVERSITY - EQUIPMENT BORROWING SYSTEM (BSU-COE-EBS)
echo Database Setup & Seed Import Tool
echo ===============================================================================

SET MYSQL_BIN=C:\xampp\mysql\bin\mysql.exe

IF NOT EXIST "%MYSQL_BIN%" (
    echo [ERROR] MySQL binary not found at %MYSQL_BIN%.
    echo Please ensure XAMPP MySQL is installed or adjust the path in this script.
    pause
    exit /b 1
)

echo [INFO] Importing database schema and seed data into MySQL...
"%MYSQL_BIN%" -u root < "%~dp0database\bulsu_equipment_db.sql"

IF %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Database 'bulsu_equipment_db' has been imported and seeded successfully!
    echo.
    echo Testing query:
    "%MYSQL_BIN%" -u root -e "USE bulsu_equipment_db; SELECT name, category_name, available_quantity FROM equipment LIMIT 5;"
) ELSE (
    echo [ERROR] Failed to import database. Please check if MySQL server is running.
)

pause
