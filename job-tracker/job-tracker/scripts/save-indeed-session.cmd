@echo off
REM Ejecutar desde PowerShell o CMD en Windows (no WSL).
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0save-indeed-session.ps1" %*
