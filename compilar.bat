@echo off
chcp 65001 >nul
echo ======================================================
echo  Compilando Gestion de Grafos Ponderados
echo  Actividad 3.3 / 3.4
echo ======================================================
echo.
echo  Equipo:
echo    - Carmona Barbosa Geovani Gael
echo    - Espinoza Lopez Manuel Angel
echo    - Leyva Perez Victor Manuel
echo ======================================================
echo.

REM Crear directorio de salida
if not exist "build\classes" mkdir build\classes

REM Compilar todos los archivos Java (encoding UTF-8 obligatorio)
javac -encoding UTF-8 -d build\classes -sourcepath src src\gestiondegrafos\*.java

if %ERRORLEVEL% == 0 (
    echo.
    echo [OK] Compilacion exitosa.
    echo.
    echo ======================================================
    echo  Generando JAR ejecutable en dist/
    echo ======================================================
    if not exist dist mkdir dist
    jar cfm dist\GestionGrafos.jar MANIFEST.MF -C build\classes .
    echo [OK] JAR generado: dist\GestionGrafos.jar
    echo.
    echo Para ejecutar: java -jar dist\GestionGrafos.jar
) else (
    echo.
    echo [ERROR] Fallo en la compilacion. Revise los mensajes anteriores.
)
pause
