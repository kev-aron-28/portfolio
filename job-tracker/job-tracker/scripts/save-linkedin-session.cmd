@echo off
REM Ejecutar desde PowerShell o CMD en Windows (no WSL).
REM Evita el bloqueo de ExecutionPolicy al correr .ps1 desde \\wsl.localhost\...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0save-linkedin-session.ps1" %*
