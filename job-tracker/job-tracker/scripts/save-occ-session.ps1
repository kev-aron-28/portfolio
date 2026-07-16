# Exporta sesión de OCC para Playwright (ejecutar en PowerShell de Windows, no en WSL).
param(
    [string]$Output = "$env:USERPROFILE\occ-storage-state.json"
)

$ErrorActionPreference = "Stop"

Write-Host "==> Instalando Chromium de Playwright (si hace falta)..."
npx --yes playwright install chromium

$wslPath = "/mnt/c/Users/$($env:USERNAME)/occ-storage-state.json"
if ($Output -ne "$env:USERPROFILE\occ-storage-state.json") {
    $wslPath = "(ruta WSL equivalente a $Output)"
}

Write-Host ""
Write-Host "==> Abriendo OCC. Inicia sesión y cierra la ventana de Playwright cuando termines."
Write-Host "    Archivo: $Output"
Write-Host ""

npx --yes playwright codegen "https://secure.occ.com.mx/Account/Login" --save-storage="$Output"

Write-Host ""
Write-Host "Sesión guardada."
Write-Host "En http://localhost:8080/scraping usa:"
Write-Host "  OCC session file: $wslPath"
