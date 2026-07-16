# Exporta sesión de Indeed México para Playwright (ejecutar en PowerShell de Windows, no en WSL).
param(
    [string]$Output = "$env:USERPROFILE\indeed-storage-state.json"
)

$ErrorActionPreference = "Stop"

Write-Host "==> Instalando Chromium de Playwright (si hace falta)..."
npx --yes playwright install chromium

$wslPath = "/mnt/c/Users/$($env:USERNAME)/indeed-storage-state.json"
if ($Output -ne "$env:USERPROFILE\indeed-storage-state.json") {
    $wslPath = "(ruta WSL equivalente a $Output)"
}

Write-Host ""
Write-Host "==> Abriendo Indeed. Completa el captcha / inicia sesión y cierra la ventana cuando termines."
Write-Host "    Archivo: $Output"
Write-Host ""

npx --yes playwright codegen "https://mx.indeed.com" --save-storage="$Output"

Write-Host ""
Write-Host "Sesión guardada."
Write-Host "En http://localhost:8080/scraping usa:"
Write-Host "  Indeed session file: $wslPath"
