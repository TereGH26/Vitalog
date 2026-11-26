@echo off
setlocal
cd /d "%~dp0"
set "VITALOG_JAR=%~dp0backend\target\vitalog-0.0.1-SNAPSHOT.jar"

set "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.13.21-hotspot"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo No se encontro Java 21 en "%JAVA_HOME%".
    exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MAVEN_VERSION=3.10.0"
set "MAVEN_HOME=%~dp0.tools\apache-maven-%MAVEN_VERSION%"
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Instalando Apache Maven %MAVEN_VERSION% localmente...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $version='%MAVEN_VERSION%'; $archive=Join-Path $env:TEMP ('apache-maven-'+$version+'-bin.zip'); $url='https://dlcdn.apache.org/maven/maven-3/'+$version+'/binaries/apache-maven-'+$version+'-bin.zip'; Invoke-WebRequest -Uri $url -OutFile $archive; Expand-Archive -LiteralPath $archive -DestinationPath '%~dp0.tools' -Force; Remove-Item $archive"
    if errorlevel 1 (
        echo No se pudo descargar Maven. Revisa la conexion y vuelve a ejecutar este archivo.
        exit /b 1
    )
)

if exist ".env" (
    for /f "usebackq eol=# tokens=1,* delims==" %%A in (".env") do set "%%A=%%B"
)

docker start mongodb >nul 2>&1

if not exist "frontend\node_modules" (
    echo Instalando dependencias del frontend...
    pushd frontend
    call npm ci
    if errorlevel 1 (popd & exit /b 1)
    popd
)

echo Compilando frontend...
pushd frontend
call npm run build
if errorlevel 1 (popd & exit /b 1)
popd

if not exist "backend\src\main\resources\static" mkdir "backend\src\main\resources\static"
xcopy "frontend\build\*" "backend\src\main\resources\static\" /E /I /Y >nul
if errorlevel 1 (
    echo No se pudieron copiar los archivos compilados del frontend.
    exit /b 1
)

echo Deteniendo una instancia anterior de VitaLog si existe...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$expected=$env:VITALOG_JAR; $processes=Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -and $_.CommandLine.Contains($expected) }; foreach ($process in $processes) { Stop-Process -Id $process.ProcessId -Force; try { Wait-Process -Id $process.ProcessId -Timeout 10 -ErrorAction SilentlyContinue } catch {} }"
if errorlevel 1 exit /b 1

echo Compilando y probando backend...
call "%MAVEN_HOME%\bin\mvn.cmd" -f "backend\pom.xml" clean package
if errorlevel 1 exit /b 1

for /f %%P in ('powershell -NoProfile -Command "$port=8080; $found=$false; while (-not $found -and $port -le 8090) { $listener=[System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback,$port); try { $listener.Start(); $found=$true } catch { $port++ } finally { $listener.Stop() } }; if ($found) { $port } else { exit 1 }"') do set "VITALOG_PORT=%%P"
if not defined VITALOG_PORT (
    echo No se encontro un puerto libre entre 8080 y 8090.
    exit /b 1
)

echo Iniciando backend en http://localhost:%VITALOG_PORT% ...
start "Vitalog backend" /min cmd /c ""%JAVA_HOME%\bin\java.exe" -jar "%VITALOG_JAR%" --server.port=%VITALOG_PORT% > "%~dp0backend\run.log" 2>&1"

for /L %%I in (1,1,30) do (
    powershell -NoProfile -Command "try { $r=Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:%VITALOG_PORT%' -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
    if not errorlevel 1 goto :ready
    timeout /t 2 /nobreak >nul
)

echo El backend no respondio a tiempo. Revisa backend\run.log.
exit /b 1

:ready
start "" "http://localhost:%VITALOG_PORT%"
echo VitaLog esta disponible en http://localhost:%VITALOG_PORT%
exit /b 0