#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT="${1:-$HOME/occ-storage-state.json}"

cd "$ROOT"

if grep -qi microsoft /proc/version 2>/dev/null; then
  export DISPLAY="${DISPLAY:-:0}"
  echo "==> WSL detectado (DISPLAY=$DISPLAY)"
  echo ""
  echo "    Si la ventana de Chromium sale en blanco, usa PowerShell en Windows:"
  echo "      cd $ROOT"
  echo "      .\\scripts\\save-occ-session.cmd"
  echo ""
  echo "    Luego en la app configura la ruta WSL, por ejemplo:"
  echo "      /mnt/c/Users/TU_USUARIO/occ-storage-state.json"
  echo ""
fi

echo "==> Compilando..."
./mvnw -q compile

echo "==> Si es la primera vez:"
echo "    sudo npx playwright install-deps chromium"
echo "    ./mvnw exec:java@playwright-install"
echo ""

./mvnw exec:java@occ-session -Docc.session.file="$OUTPUT"
