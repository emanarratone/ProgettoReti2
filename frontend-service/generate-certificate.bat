@echo off
REM Script to generate self-signed TLS certificate for Spring Boot
REM This creates a keystore file for HTTPS configuration

echo Generating self-signed TLS certificate for frontend-service...

REM Set variables
set KEYSTORE_PATH=src\main\resources\server-keystore.jks
set KEYSTORE_PASSWORD=changeit
set ALIAS=frontend-service
set VALIDITY=365

REM Remove existing keystore if it exists
if exist "%KEYSTORE_PATH%" (
    echo Removing existing keystore...
    del "%KEYSTORE_PATH%"
)

REM Generate the keystore with self-signed certificate
echo Generating keystore with self-signed certificate...
keytool -genkeypair ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity %VALIDITY% ^
    -alias %ALIAS% ^
    -keystore "%KEYSTORE_PATH%" ^
    -storepass %KEYSTORE_PASSWORD% ^
    -keypass %KEYSTORE_PASSWORD% ^
    -dname "CN=localhost, OU=Frontend Service, O=AutoStrada, C=IT" ^
    -ext san=dns:localhost,dns:*.localhost,ip:127.0.0.1

echo.
echo Certificate generation completed successfully!
echo Keystore file created at: %KEYSTORE_PATH%
echo Keystore password: %KEYSTORE_PASSWORD%
echo Alias: %ALIAS%
echo.
echo The application is configured to use this certificate for HTTPS.
echo All microservices must also be configured with HTTPS certificates.
pause
