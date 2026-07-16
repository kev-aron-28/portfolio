@echo off
REM Ejecutar desde PowerShell o CMD en Windows (no WSL).
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0save-occ-session.ps1" %*
